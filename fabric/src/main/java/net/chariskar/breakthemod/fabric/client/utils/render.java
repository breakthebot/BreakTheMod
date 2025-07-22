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

package net.chariskar.breakthemod.fabric.client.utils;

import net.chariskar.breakthemod.engine.nearby;
import net.chariskar.breakthemod.fabric.client.commands.FabricCommand;
import net.chariskar.breakthemod.fabric.client.utils.wrappers.player;
import net.chariskar.breakthemod.fabric.client.utils.wrappers.world;
import net.chariskar.breakthemod.utils.config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class render {

    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private static List<String> playerList = new ArrayList<>();
    private static final int MARGIN = 10;

    private static final int customX = config.getInstance().getCustomX();
    private static final int customY = config.getInstance().getCustomY();
    private static config.WidgetPosition widgetPosition = config.getInstance().getWidgetPosition();
    private static final nearby engine = new nearby();

    /**
     * @param drawContext The draw context {@link net.minecraft.client.gui.DrawContext}
     * @param tickCounter The tickCounter {@link net.minecraft.client.render.RenderTickCounter}
     * Renders the overlay.
     */
    public static void renderOverlay(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.options.hudHidden || client.world == null || client.player == null) return;

        config Config = config.getInstance();

        if (!Config.getRadarEnabled()) return;
        if (!FabricCommand.getEnabledOnOtherServers()) return;

        widgetPosition = Config.getWidgetPosition();
        TextRenderer textRenderer = client.textRenderer;

        Set<String> nearbyPlayersSet = engine.updateNearbyPlayers(
                new player(client.player),
                new world(client.world)
        );

        playerList = new ArrayList<>(nearbyPlayersSet);

        boolean hasPlayersInChunks = hasPlayersInRenderedChunks(client);
        boolean hasNearbyPlayers = !playerList.isEmpty() && !playerList.contains("No players nearby");

        if (!hasPlayersInChunks || !hasNearbyPlayers) {
            playerList.clear();
            playerList.add("No players nearby");
        }

        int entryHeight = 15;
        int width = playerList.stream()
                .mapToInt(textRenderer::getWidth)
                .max()
                .orElse(100) + 2 * MARGIN;
        int height = Math.max(20 + playerList.size() * entryHeight, 40);

        int x = 0, y = 0;

        switch (widgetPosition) {
            case TOP_RIGHT -> {
                x = client.getWindow().getScaledWidth() - width - MARGIN;
                y = MARGIN;
            }
            case BOTTOM_LEFT -> {
                x = MARGIN;
                y = client.getWindow().getScaledHeight() - height - MARGIN;
            }
            case BOTTOM_RIGHT -> {
                x = client.getWindow().getScaledWidth() - width - MARGIN;
                y = client.getWindow().getScaledHeight() - height - MARGIN;
            }
            case CUSTOM -> {
                x = customX;
                y = customY;
            }
            case TOP_LEFT -> {
                x = MARGIN;
                y = MARGIN;
            }
        }

        int textY = y + 5;
        synchronized (playerList) {
            for (String line : playerList) {
                int color = line.equals("No players nearby") ? 0xFF6B6B : 0xFFFFFF;
                drawContext.drawText(textRenderer, line, x + MARGIN, textY, color, false);
                textY += entryHeight;
            }
        }
    }

    /**
     * Returns true if there are players in the loaded chunks, false if not.
     * @param client The minecraft client {@link net.minecraft.client.MinecraftClient}
     * @return If there are any players in the rendered chunks.
     */
    private static boolean hasPlayersInRenderedChunks(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return false;
        }

        var chunkManager = client.world.getChunkManager();

        for (var player : client.world.getPlayers()) {
            if (player == client.player) {
                continue;
            }

            int chunkX = (int) Math.floor(player.getX()) >> 4;
            int chunkZ = (int) Math.floor(player.getZ()) >> 4;

            var chunk = chunkManager.getChunk(chunkX, chunkZ);
            if (chunk != null) {
                return true;
            }
        }

        return false;
    }
}