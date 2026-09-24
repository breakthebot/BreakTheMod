import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.gradleup.shadow)
}

group = project.property("maven_group") as String
version = project.property("mod_version") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 25

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
        allWarningsAsErrors.set(true)
    }
}

fabricApi {
    configureDataGeneration {
        client = false
    }
}

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.nucleoid.xyz/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.isxander.dev/releases")
    maven("https://jitpack.io")
    maven("https://storage.googleapis.com/r8-releases/raw")

    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val shade = configurations.create("shade")
val r8 = configurations.create("r8")

val debug = project.hasProperty("debug")
val release = project.hasProperty("release")
val github = project.hasProperty("github")

dependencies {
    minecraft(libs.minecraft)

    api(libs.fabric.loader)
    api(libs.fabric.api)
    api(libs.fabric.kotlin)

    implementation(libs.placeholder.api)

    api(libs.cloth.config) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    api(libs.modmenu)

    implementation(libs.breakthelibrary)
    shade(libs.breakthelibrary) {
        isTransitive = false
    }

    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
    compileOnly(libs.kotlinx.serialization.json)
    r8(libs.r8)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to version,
                "minecraft_version" to libs.versions.mc,
                "loader_version" to libs.fabric.loader,
                "kotlin_loader_version" to libs.fabric.kotlin,
                "cloth_config" to libs.cloth.config,
                "placeholder_api" to libs.placeholder.api,
                "modmenu" to libs.modmenu,
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(targetJavaVersion)
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

val headerText = file("header.txt").takeIf { it.exists() }?.readText()

val addHeader = tasks.register("AddHeader") {
    description = "Adds a license header to every file."
    onlyIf { headerText != null }

    doLast {
        fileTree("src") {
            include("**/*.kt", "**/*.java")
        }.forEach { file ->
            val content = file.readText()
            if (content.startsWith("///")) {
                return@forEach
            }
            if (!content.startsWith(headerText!!)) {
                file.writeText("$headerText\n$content")
            }
        }
    }
}
val shadowJarTask = tasks.named<ShadowJar>("shadowJar") {
    group = "build"
    description = "Add the dependencies to the jar."

    archiveClassifier.set("dev-shadow")
    configurations = listOf(shade)

    relocate(
        "org.breakthebot.breakthelibrary",
        "${project.group}.shadow.breakthelibrary"
    )
}

// Yea ik this is extreme and theres probably a better way, but i cant find it rn.
// TODO: Maybe find a better way for this?

val shrinkJar = tasks.register<JavaExec>("shrinkJar") {
    group = "build"
    description = "Shrink the output jar while preserving Mixin classes."

    dependsOn(shadowJarTask)

    classpath = r8
    mainClass.set("com.android.tools.r8.R8")

    val inputJar = shadowJarTask.flatMap { it.archiveFile }

    val workDir = layout.buildDirectory.dir("r8-work")
    val r8InputJar = layout.buildDirectory.file("r8-work/r8-input.jar")
    val r8OutputJar = layout.buildDirectory.file("r8-work/r8-output.jar")

    val outputJar = layout.buildDirectory.file(
        "libs/${project.name.lowercase()}-${project.version}-min.jar"
    )

    val libraryJars = configurations.runtimeClasspath
    val javaHome = System.getProperty("java.home")

    val preservedPrefix = "net/chariskar/breakthemod/mixins/"

    doFirst {
        val work = workDir.get().asFile
        val input = inputJar.get().asFile
        val r8Input = r8InputJar.get().asFile

        delete(work)
        work.mkdirs()

        ZipFile(input).use { zip ->
            ZipOutputStream(r8Input.outputStream()).use { output ->

                zip.entries().asSequence().forEach { entry ->

                    if (entry.name.startsWith(preservedPrefix)) {
                        return@forEach
                    }

                    val newEntry = ZipEntry(entry.name)

                    newEntry.time = entry.time

                    output.putNextEntry(newEntry)

                    if (!entry.isDirectory) {
                        zip.getInputStream(entry).use { inputStream ->
                            inputStream.copyTo(output)
                        }
                    }

                    output.closeEntry()
                }
            }
        }
    }

    args(
        "--release",
        "--classfile",

        "--output",
        r8OutputJar.get().asFile.absolutePath,

        "--pg-conf",
        file("r8-rules.pro").absolutePath,

        r8InputJar.get().asFile.absolutePath,

        "--lib",
        javaHome,

        *libraryJars.get()
            .filter { it.extension == "jar" }
            .flatMap {
                listOf("--lib", it.absolutePath)
            }
            .toTypedArray()
    )

    doLast {
        val input = inputJar.get().asFile
        val r8Output = r8OutputJar.get().asFile
        val finalOutput = outputJar.get().asFile

        finalOutput.parentFile.mkdirs()

        val preservedEntries = mutableMapOf<String, ByteArray>()

        ZipFile(input).use { zip ->
            zip.entries().asSequence()
                .filter { entry ->
                    entry.name.startsWith(preservedPrefix)
                }
                .forEach { entry ->
                    preservedEntries[entry.name] =
                        zip.getInputStream(entry).use { it.readBytes() }
                }
        }

        ZipFile(r8Output).use { r8Zip ->
            ZipOutputStream(finalOutput.outputStream()).use { output ->

                r8Zip.entries().asSequence().forEach { entry ->
                    val newEntry = ZipEntry(entry.name)
                    newEntry.time = entry.time

                    output.putNextEntry(newEntry)

                    if (!entry.isDirectory) {
                        r8Zip.getInputStream(entry).use { inputStream ->
                            inputStream.copyTo(output)
                        }
                    }

                    output.closeEntry()
                }

                preservedEntries.forEach { (name, bytes) ->
                    val entry = ZipEntry(name)

                    output.putNextEntry(entry)
                    output.write(bytes)
                    output.closeEntry()
                }
            }
        }
    }
}

val debugPackage = "net/chariskar/breakthemod/debug/**"

tasks.jar {
    dependsOn(shadowJarTask)

    inputs.file(shadowJarTask.flatMap { it.archiveFile })
    archiveClassifier.set(null as String?)
}

val versionTask = tasks.register<Exec>("versionAdd") {
    description = "Add the current version to version.json."

    onlyIf { release && !github }

    commandLine(
        "kotlin",
        "src/scripts/Version.main.kts",
        version.toString(),
        "true",
        libs.versions.mc
    )
}

if (release) {
    tasks.jar {
        enabled = false
    }
}

tasks.build {
    dependsOn(addHeader, versionTask)
}

tasks.named<ShadowJar>("shadowJar") {
    if (!debug) {
        exclude(debugPackage)
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
