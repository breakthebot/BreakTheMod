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

package net.chariskar.breakthemod.client.modules

import net.chariskar.breakthemod.client.api.BaseModule
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

object ShopTracker : BaseModule(){
    override val name = "ShopTracker"
    override val description = "Tracks when one of your shops empties for the getShops command."

    private val shopRegex = Regex(
        """at\s+(-?\d+)\s*,\s*(-?\d+)\s*,\s*(-?\d+).*?run out of\s+(.+?)!?$"""
    )

    val emptyShops: MutableList<ShopObject> = mutableListOf()

    override fun disable() {
        emptyShops.clear()
        enabled = false
    }
    
    data class ShopObject(
        val coords: Vec3d,
        val item: String      
    ) {
        override fun toString(): String {
            return "-Shop at ${coords.x}, ${coords.y}, ${coords.z} ($item)"
        }
    }

    override fun enable() {
        ClientReceiveMessageEvents.GAME.register(ClientReceiveMessageEvents.Game { message: Text?, _: Boolean ->
            if (!ServerUtils.isEarthMc()) return@Game

            val string = message?.string ?: return@Game

            if (!string.contains("Your shop at")) return@Game

            val shop = parseShopObject(string) ?: return@Game

            emptyShops.add(shop)
            logDebug("Shop has been added.")
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            emptyShops.clear()
        })
        
    }

    fun parseShopObject(message: String): ShopObject? {
        val match = shopRegex.find(message)
        val (x, y, z, item) = match?.destructured ?: return null

        return ShopObject(
            Vec3d(
                x.toDouble(),
                y.toDouble(),
                z.toDouble()
            ),
            item
        )
    }

}