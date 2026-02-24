import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.10"
    id("fabric-loom") version "1.11-SNAPSHOT"
    kotlin("plugin.serialization") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"

}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}


fabricApi {
    configureDataGeneration {
        client = false
    }
}

repositories {
    maven ("https://maven.terraformersmc.com/") {
        name = "Terraformers"
    }
    maven ("https://maven.nucleoid.xyz/") {
        name = "Nucleoid"
    }
    maven("https://maven.shedaniel.me/")
    maven("https://maven.isxander.dev/releases")
    maven("https://jitpack.io")
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

}

val shade: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isVisible = false
}

val mcVer = project.property("minecraft_version")
val mappings = project.property("yarn_mappings")

val fabricVersion = project.property("fabric_version")
val fabricLoader = project.property("loader_version")
val kotlinLoader = project.property("kotlin_loader_version")
val ktSere = project.property("kt_sere")

val xaerosVersion = project.property("xaeros_version")
val clothVersion = project.property("cloth_config")
val modmenu = project.property("modmenu")
val placeholderVersion = project.property("placeholder_api")

dependencies {
    minecraft("com.mojang:minecraft:$mcVer")
    mappings("net.fabricmc:yarn:$mappings:v2")

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$ktSere")

    modImplementation("net.fabricmc:fabric-loader:$fabricLoader")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinLoader")

    modImplementation("eu.pb4:placeholder-api:$placeholderVersion")

    modApi("me.shedaniel.cloth:cloth-config-fabric:$clothVersion") {
        exclude("net.fabricmc.fabric-api")
    }

    modApi("com.terraformersmc:modmenu:$modmenu")

    implementation("com.github.breakthebot:breakthelibrary:1.0.4")
    shade("com.github.breakthebot:breakthelibrary:1.0.4") {
        isTransitive = false
    }

    modImplementation("maven.modrinth:xaeros-minimap:${project.property("xaeros_version")}")

}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to mcVer,
            "loader_version" to fabricLoader,
            "kotlin_loader_version" to kotlinLoader,
            "cloth_config" to clothVersion,
            "placeholder_api" to placeholderVersion,
            "modmenu" to modmenu
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

val headerText = file("header.txt").readText()

val addHeader by tasks.registering {
    group = "build"

    val targetFiles = fileTree("src") {
        include("**/*.kt")
        include("**/*.java")
    }

    doLast {
        targetFiles.forEach { file: File ->
            val content = file.readText()
            if (!content.startsWith(headerText)) {
                file.writeText("$headerText\n$content")
            }
        }
    }
}

tasks.shadowJar {
    dependsOn(tasks.jar)

    archiveClassifier.set("shadow-dev")

    configurations = listOf(shade)

    from(zipTree(tasks.jar.get().archiveFile))

    relocate(
        "org.breakthebot.breakthelibrary",
        "net.chariskar.breakthebot.breakthelibrary"
    )
}

val remapShadowJar by tasks.registering(net.fabricmc.loom.task.RemapJarTask::class) {
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    archiveClassifier.set("shadowed")

    doLast {
        delete(tasks.shadowJar.get().archiveFile.get().asFile)
    }
}

tasks["build"].dependsOn(addHeader, remapShadowJar)

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}
