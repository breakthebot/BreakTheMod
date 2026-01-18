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
package net.chariskar.breakthemod.client.hooks

import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

object PlayerEvents {
    public fun onServerJoin() {
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            NearbyEngine.engineRunning = true
        }
    }

    public fun onServerLeave() {
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            NearbyEngine.engineRunning = false
        }
    }

    public fun init(){
        onServerJoin()
        onServerLeave()
    }
}