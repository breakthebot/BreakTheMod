package net.chariskar.breakthemod.client.objects

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val x: Double? = null,
    val y: Double? = null,
    val z: Double? = null
)