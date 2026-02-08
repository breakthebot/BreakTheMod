package net.chariskar.breakthemod.client.objects

import kotlinx.serialization.Serializable

@Serializable
data class Location (
    val name: String? = null,
    val location: Coordinates? = null,
    val isWilderness: Boolean? = null,
    val town: Reference? = null,
    val nation: Reference? = null
)