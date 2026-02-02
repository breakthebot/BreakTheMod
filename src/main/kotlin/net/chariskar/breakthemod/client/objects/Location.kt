package net.chariskar.breakthemod.client.objects

import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.utils.serialization.SerializableUUID

@Serializable
data class Location (
    val name: String? = null,
    val location: Coordinates? = null,
    val isWilderness: Boolean? = null,
    val town: Town? = null,
    val nation: Nation? = null
) {
    @Serializable
    data class Coordinates(
        val x: Double? = null,
        val z: Double? = null
    )

    @Serializable
    data class Town (
        val name: String? = null,
        val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Nation (
        val name: String? = null,
        val uuid: SerializableUUID? = null
    )
}