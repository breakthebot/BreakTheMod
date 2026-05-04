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

import net.chariskar.breakthemod.client.api.Module
import net.chariskar.breakthemod.client.modules.Cache.cachedPlayers

import net.chariskar.breakthemod.client.utils.ChatChannel
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text

object ShopTracker : Module(){
    val regex = Regex("""at\s+(-?\d+)\s+(-?\d+)\s+(-?\d+).*?run out of\s+([a-z_]+)""")
    val emptyShops: MutableList<ShopObject> = mutableListOf()

    override fun disable() {
        emptyShops.clear()
        enabled = false
    }
    
    data class ShopObject(
        val x: Float,
        val y: Float,
        val z: Float,
        val item: String      
    ) {
        override fun toString(): String {
            return "Your shop of $item at $x $y $z has run out."
        }
    }

    override fun enable() {
        ClientReceiveMessageEvents.GAME.register(ClientReceiveMessageEvents.Game { message: Text?, _: Boolean ->
            if (!ServerUtils.isEarthMc()) return@Game
            val string = message?.string ?: return@Game

            if (!string.contains("Your shop has run out of")) return@Game

            val match = regex.find(string)

            if (match != null) {
                val (x, y, z, item) = match.destructured
                emptyShops.add(
                    ShopObject(x.toFloat(), y.toFloat(), z.toFloat(), item)
                )
            }
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            emptyShops.clear()
        })

        
    }
}