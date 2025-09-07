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

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Resident(val name: String) {
    var uuid: UUID? = null
    var title: String? = null
    var surname: String? = null
    var formattedName: String? = null
    var about: String? = null
    var town: Reference? = null
    var nation: Reference? = null
    var timestamps: Timestamps? = null
    var status: Status? = null
    var stats: Stats? = null
    var perms: Perms? = null
    var ranks: Ranks? = null
    var friends: List<Reference>? = null

    data class Timestamps(
        var registered: Long? = null,
        var joinedTownAt: Long? = null,
        var lastOnline: Long? = null
    )

    data class Status(
        var isOnline: Boolean? = null,
        var isNPC: Boolean? = null,
        var isMayor: Boolean? = null,
        var isKing: Boolean? = null,
        var hasTown: Boolean? = null,
        var hasNation: Boolean? = null
    )

    data class Stats(
        var balance: Int? = null,
        var numFriends: Int? = null
    )

    data class Perms(
        var build: List<Boolean>? = null,
        var destroy: List<Boolean>? = null,
        @SerializedName("switch")
        var switchPerm: List<Boolean>? = null,
        var itemUse: List<Boolean>? = null,
        var flags: Flags? = null
    )

    data class Flags(
        var pvp: Boolean? = null,
        var explosion: Boolean? = null,
        var fire: Boolean? = null,
        var mobs: Boolean? = null
    )

    data class Ranks(
        var townRanks: List<String>? = null,
        var nationRanks: List<String>? = null
    )
}
