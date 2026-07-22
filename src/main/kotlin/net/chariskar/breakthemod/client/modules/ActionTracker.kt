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

import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.api.widget.WidgetManager
import net.chariskar.breakthemod.client.api.widget.WidgetModes
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import net.minecraft.stats.Stats
import net.minecraft.world.level.block.Blocks
import kotlin.time.Clock
import kotlin.time.Instant

object ActionTracker : BaseModule(
    "Action Tracker",
    "Tracks data for mining and fishing widgets.",
    true
) {
    var goldMined = 0

    var fishingModeActivated: Instant? = null

    val timeFishing: Long
        get() = (fishingModeActivated?.minus(Clock.System.now()))?.inWholeMinutes ?: 0

    val fishFished
        get() = client.player?.stats?.getValue(
            Stats.CUSTOM.get(Stats.FISH_CAUGHT)
        ) ?: 0

    override fun enable() {
        if (!isModEnabled()) return
        ClientPlayerBlockBreakEvents.AFTER.register(
            ClientPlayerBlockBreakEvents.After { _, _, _, state ->
                if (
                    (state.`is`(Blocks.GOLD_BLOCK) || state.`is`(Blocks.GOLD_ORE) || state.`is`(Blocks.RAW_GOLD_BLOCK)) &&
                    WidgetManager.widgetMode == WidgetModes.Mining
                ) {
                    goldMined++
                }
            }
        )
    }
}
