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
import java.util.UUID

@Serializable
data class Town(
    val name: String,
    val uuid: UUID? = null,
    val board: String? = null,
    val founder: String? = null,
    val wiki: String? = null,
    val mayor: Mayor? = null,
    val nation: Nation? = null,
    val timestamps: Timestamps? = null,
    val status: Status? = null,
    val stats: Stats? = null,
    val perms: Perms? = null,
    val coordinates: Coordinates? = null,
    val residents: List<Resident>? = null,
    val trusted: List<Resident>? = null,
    val outlaws: List<Resident>? = null,
    val quarters: List<Reference>? = null,
    val ranks: Ranks? = null
) {
    data class Mayor(
        val name: String? = null,
        val uuid: UUID? = null
    )

    data class Nation(
        val name: String? = null,
        val uuid: UUID? = null
    )

    data class Timestamps(
        val registered: Long? = null,
        val joinedNationAt: Long? = null,
        val ruinedAt: Long? = null
    )

    data class Status(
        val isPublic: Boolean? = null,
        val isOpen: Boolean? = null,
        val isNeutral: Boolean? = null,
        val isCapital: Boolean? = null,
        val isOverClaimed: Boolean? = null,
        val isRuined: Boolean? = null,
        val isForSale: Boolean? = null,
        val hasNation: Boolean? = null,
        val hasOverclaimShield: Boolean? = null,
        val canOutsidersSpawn: Boolean? = null
    )

    data class Stats(
        val numTownBlocks: Int? = null,
        val maxTownBlocks: Int? = null,
        val numResidents: Int? = null,
        val numTrusted: Int? = null,
        val numOutlaws: Int? = null,
        val balance: Int? = null,
        val forSalePrice: Int? = null
    )

    data class Perms(
        val build: List<Boolean>? = null,
        val destroy: List<Boolean>? = null,
        val switchPerm: List<Boolean>? = null,
        val itemUse: List<Boolean>? = null,
        val flags: Flags? = null
    )

    data class Flags(
        val pvp: Boolean? = null,
        val explosion: Boolean? = null,
        val fire: Boolean? = null,
        val mobs: Boolean? = null
    )

    data class Coordinates(
        val spawn: Spawn? = null,
        val homeBlock: List<Int>? = null,
        val townBlocks: List<List<Int>>? = null
    )

    data class Spawn(
        val world: String? = null,
        val x: Double? = null,
        val y: Int? = null,
        val z: Double? = null,
        val pitch: Float? = null,
        val yaw: Float? = null
    )

    data class Resident(
        val name: String? = null,
        val uuid: UUID? = null
    )

    data class Ranks(
        val councillor: List<Resident>? = null,
        val builder: List<Resident>? = null,
        val recruiter: List<Resident>? = null,
        val police: List<Resident>? = null,
        val taxExempt: List<Resident>? = null,
        val treasurer: List<Resident>? = null,
        val realtor: List<Resident>? = null,
        val settler: List<Resident>? = null
    )
}