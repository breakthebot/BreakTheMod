#!/usr/bin/env kotlin

@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class Version(
    val version: String,
    var latest: Boolean,
    val release: Boolean,
    val mcVer: String,
)

data class VersionFile(
    val versions: MutableList<Version>,
)

val mapper = jacksonObjectMapper()

val newVersion = Version(
    args[0],
    true,
    args[1].toBoolean(),
    args[2]
)

val releaseFile = File("version.json")

val versions: VersionFile = mapper.readValue(releaseFile)

versions.versions.forEach {
    if (it.version == newVersion.version) {
        throw Exception("Version does not exist.")
    }
    it.latest = false
}

versions.versions.add(newVersion)

mapper.writerWithDefaultPrettyPrinter().writeValue(releaseFile, versions)
