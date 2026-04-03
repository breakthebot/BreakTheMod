import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "1.9.10"

    id("fabric-loom") version "1.15-SNAPSHOT"
    id("com.gradleup.shadow") version "9.2.0"
}

group = project.property("maven_group") as String
version = project.property("mod_version") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    withSourcesJar()
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

    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val shade by configurations.creating

val mcVer: String by project
val mappings: String by project
val fabricVersion: String by project
val fabricLoader: String by project
val kotlinLoader: String by project
val ktSere: String by project
val clothVersion: String by project
val modmenu: String by project
val placeholderVersion: String by project
val breakTheLibrary: String by project

dependencies {
    minecraft("com.mojang:minecraft:$mcVer")
    mappings("net.fabricmc:yarn:$mappings:v2")

    modImplementation("net.fabricmc:fabric-loader:$fabricLoader")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinLoader")

    modImplementation("eu.pb4:placeholder-api:$placeholderVersion")

    modApi("me.shedaniel.cloth:cloth-config-fabric:$clothVersion") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modApi("com.terraformersmc:modmenu:$modmenu")

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
                "modmenu" to modmenu
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

val addHeader by tasks.registering {
    onlyIf { headerText != null }

    doLast {
        fileTree("src") {
            include("**/*.kt", "**/*.java")
        }.forEach { file ->
            val content = file.readText()
            if (!content.startsWith(headerText!!)) {
                file.writeText("$headerText\n$content")
            }
        }
    }
}

val shadowJarTask = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("dev-shadow")
    configurations = listOf(shade)

    relocate(
        "org.breakthebot.breakthelibrary",
        "${project.group}.shadow.breakthelibrary"
    )
}

tasks.remapJar {
    dependsOn(shadowJarTask)

    inputFile.set(shadowJarTask.flatMap { it.archiveFile })
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


tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}