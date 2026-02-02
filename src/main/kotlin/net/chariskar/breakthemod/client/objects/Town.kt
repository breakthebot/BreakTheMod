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
package net.chariskar.breakthemod.client.api.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.utils.serialization.SerializableUUID

@Serializable
data class Town(
    val name: String,
    val uuid: SerializableUUID? = null,
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
    @Serializable
    data class Mayor(
        val name: String? = null,
        val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Nation(
        val name: String? = null,
        val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Timestamps(
        val registered: Long? = null,
        val joinedNationAt: Long? = null,
        val ruinedAt: Long? = null
    )

    @Serializable
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

    @Serializable
    data class Stats(
        val numTownBlocks: Int? = null,
        val maxTownBlocks: Int? = null,
        val numResidents: Int? = null,
        val numTrusted: Int? = null,
        val numOutlaws: Int? = null,
        val bonusBlocks: Int?  = null,
        val balance: Float? = null,
        val forSalePrice: Float? = null
    )

    @Serializable
    data class Perms(
        val build: List<Boolean>? = null,
        val destroy: List<Boolean>? = null,
        val switchPerm: List<Boolean>? = null,
        val itemUse: List<Boolean>? = null,
        val switch: List<Boolean>? = null,
        val flags: Flags? = null
    )

    @Serializable
    data class Flags(
        val pvp: Boolean? = null,
        val explosion: Boolean? = null,
        val fire: Boolean? = null,
        val mobs: Boolean? = null
    )

    @Serializable
    data class Coordinates(
        val spawn: Spawn? = null,
        val homeBlock: List<Int>? = null,
        val townBlocks: List<List<Int>>? = null
    )

    @Serializable
    data class Spawn(
        val world: String? = null,
        val x: Float? = null,
        val y: Float? = null,
        val z: Float? = null,
        val pitch: Float? = null,
        val yaw: Float? = null
    )

    @Serializable
    data class Resident(
        val name: String? = null,
        val uuid: SerializableUUID? = null
    )

    @Serializable
    data class Ranks(
        @SerialName("Councilor") val councillor: List<Resident>? = null,
        @SerialName("Builder") val builder: List<Resident>? = null,
        @SerialName("Recruiter") val recruiter: List<Resident>? = null,
        @SerialName("Police") val police: List<Resident>? = null,
        @SerialName("Tax-Exempt") val taxExempt: List<Resident>? = null,
        @SerialName("Treasurer") val treasurer: List<Resident>? = null,
        @SerialName("Realtor") val realtor: List<Resident>? = null,
        @SerialName("Settler") val settler: List<Resident>? = null
    )

}