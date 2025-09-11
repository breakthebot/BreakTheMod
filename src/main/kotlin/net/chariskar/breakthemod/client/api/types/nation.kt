/*
 * This file is part of breakthemod.
 *
 * breakthemod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemod. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.client.api.types

import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.utils.SerializableUUID


@Serializable
class Nation(val name: String) {
    val uuid: SerializableUUID? = null
    var board: String? = null
    var dynmapColour: String? = null
    var dynmapOutline: String? = null
    var wiki: String? = null
    var king: Leader? = null
    var capital: Capital? = null
    var timestamps: Timestamps? = null
    var status: Status? = null
    var stats: Stats? = null
    var coordinates: Coordinates? = null
    var residents: List<Resident>? = null
    var towns: List<Resident>? = null
    var allies: List<Resident>? = null
    var enemies: List<Resident>? = null
    var sanctioned: List<Resident>? = null
    var ranks: Ranks? = null

    @Serializable
    data class Leader(
        var name: String? = null,
    val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Capital(
        var name: String? = null,
    val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Timestamps(
        var registered: Long? = null
    )
    @Serializable
    data class Status(
        var isPublic: Boolean? = null,
        var isOpen: Boolean? = null,
        var isNeutral: Boolean? = null
    )

    @Serializable
    data class Stats(
        var numTownBlocks: Int? = null,
        var numResidents: Int? = null,
        var numTowns: Int? = null,
        var numAllies: Int? = null,
        var numEnemies: Int? = null,
        var balance: Float? = null
    )

    @Serializable
    data class Coordinates(
        var spawn: Spawn? = null
    )

    @Serializable
    data class Spawn(
        var world: String? = null,
        var x: Double? = null,
        var y: Double? = null,
        var z: Double? = null,
        var pitch: Float? = null,
        var yaw: Float? = null
    )

    @Serializable
    data class Resident(
        var name: String? = null,
    val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Ranks(
        var Chancellor: List<Resident>? = null,
        var Colonist: List<Resident>? = null,
        var Diplomat: List<Resident>? = null
    )
}
