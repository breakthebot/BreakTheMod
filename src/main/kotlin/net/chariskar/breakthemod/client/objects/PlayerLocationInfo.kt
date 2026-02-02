package net.chariskar.breakthemod.client.objects

import kotlinx.serialization.Serializable

@Serializable
data class PlayerLocationInfo(
    val username: String,
    var x: Double,
    var z: Double,
    var isWilderness: Boolean,
    var townName: String?,
    var found: Boolean
) {

    init {
        this.found = found
    }

    override fun toString(): String {
        return if (!found) {
            "$username is either offline or not showing up on the map."
        } else if (isWilderness) {
            "$username at x: $x, z: $z is in wilderness."
        } else {
            "$username at x: $x, z: $z is in town: $townName."
        }
    }
}