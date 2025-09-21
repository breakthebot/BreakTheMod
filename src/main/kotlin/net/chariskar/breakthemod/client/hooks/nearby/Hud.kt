/*
 * This file is part of breakthemodRewrite.
 *
 * breakthemodRewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodRewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodRewrite. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.client.hooks.nearby

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Config.WidgetPosition
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import java.util.Collections

class Hud {

    private val client = MinecraftClient.getInstance()
    private val engine = NearbyEngine.getInstance()
    private val engineScope: CoroutineScope = NearbyEngine.scope
    private val playerList: MutableList<String> = Collections.synchronizedList(mutableListOf())

    private val entryHeight = 15
    private val margin = 10

    var widgetPosition: Config.Widget = Config.getWidgetPos()
    private var x: Int = 0
    private var y: Int = 0

    init {
        engineScope.launch {
            while (Config.getRadar()) {
                val players = engine.getPlayers()
                playerList.clear()
                if (players.isEmpty()) {
                    playerList.add("No players nearby")
                } else {
                    playerList.addAll(players.map { it.toString() })
                }
                kotlinx.coroutines.delay(200)
            }
        }

    }

    @Synchronized
    fun renderOverlay(drawContext: DrawContext, tickCounter: RenderTickCounter) {
        if (client.options.hudHidden || client.world == null || client.player == null) return
        if (!Config.getRadar() || !Config.getEnabledServers()) return

        val textRender: TextRenderer = client.textRenderer

        val width = (playerList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * margin

        val height = (20 + playerList.size * entryHeight).coerceAtLeast(40)

        widgetPosition = Config.getWidgetPos()
        when (widgetPosition.widgetPosition) {
            WidgetPosition.TOP_LEFT -> {
                x = margin
                y = margin
            }
            WidgetPosition.TOP_RIGHT -> {
                x = client.window.scaledWidth - width - margin
                y = margin
            }
            WidgetPosition.BOTTOM_LEFT -> {
                x = margin
                y = client.window.scaledHeight - height - margin
            }
            WidgetPosition.BOTTOM_RIGHT -> {
                x = client.window.scaledWidth - width - margin
                y = client.window.scaledHeight - height - margin
            }
            WidgetPosition.CUSTOM -> {
                x = widgetPosition.customX
                y = widgetPosition.customY
            }
        }

        var textY = y + 5
        synchronized(playerList) {
            for (line in playerList) {
                val color = if (line == "No players nearby") 0xFF6B6B else 0xFFFFFF
                drawContext.drawText(textRender, line, x + margin, textY, color, false)
                textY += entryHeight
            }
        }
    }
}
