import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"

    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    id("com.gradleup.shadow") version "9.4.3"
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
    google()

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
val debug = project.hasProperty("debug")

val mcVer = project.findProperty("mcVer") as String
val fabricVersion = project.findProperty("fabricVersion") as String
val fabricLoader = project.findProperty("fabricLoader") as String
val kotlinLoader = project.findProperty("kotlinLoader") as String
val ktSere = project.findProperty("ktSere") as String
val clothVersion = project.findProperty("clothVersion") as String
val modmenu = project.findProperty("modmenu") as String
val placeholderVersion = project.findProperty("placeholderVersion") as String
val breakTheLibrary = project.findProperty("breakTheLibrary") as String

dependencies {
    minecraft("com.mojang:minecraft:$mcVer")

    implementation("net.fabricmc:fabric-loader:$fabricLoader")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    implementation("net.fabricmc:fabric-language-kotlin:$kotlinLoader")

    implementation("eu.pb4:placeholder-api:$placeholderVersion")

    api("me.shedaniel.cloth:cloth-config-fabric:$clothVersion") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    api("com.terraformersmc:modmenu:$modmenu")

    implementation("com.github.breakthebot:BreakTheLibrary:$breakTheLibrary")
    shade("com.github.breakthebot:BreakTheLibrary:$breakTheLibrary") {
        isTransitive = false
    }

    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$ktSere")
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to version,
                "minecraft_version" to mcVer,
                "loader_version" to fabricLoader,
                "kotlin_loader_version" to kotlinLoader,
                "cloth_config" to clothVersion,
                "placeholder_api" to placeholderVersion,
                "modmenu" to modmenu,
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
    archiveClassifier.set("dev-shadow")
    configurations = listOf(shade)

    relocate(
        "org.breakthebot.breakthelibrary",
        "${project.group}.shadow.breakthelibrary"
    )
}

val debugPackage = "net/chariskar/breakthemod/debug/**"

tasks.jar {
    dependsOn(shadowJarTask)

    inputs.file(shadowJarTask.flatMap { it.archiveFile })
    archiveClassifier.set(null as String?)
}

tasks.build {
    dependsOn(addHeader)
}

if (project.hasProperty("release")) {
    tasks.jar {
        enabled = false
    }
}

tasks.named<ShadowJar>("shadowJar") {
    if (!debug) {
        exclude(debugPackage)
    }
    exclude("**/models/*Pursuit*.class")
    exclude("**/models/*ServerInfo*.class")
    exclude("**/models/*MysteryMaster*.class")
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
