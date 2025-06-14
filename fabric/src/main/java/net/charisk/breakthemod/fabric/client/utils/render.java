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

package net.charisk.breakthemod.fabric.client.utils;
import net.charisk.breakthemod.utils.config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.charisk.breakthemod.utils.config.WidgetPosition;
import java.util.*;
import net.charisk.breakthemod.fabric.client.commands.FabricCommand;
import net.charisk.breakthemod.engine.nearby;
import net.charisk.breakthemod.fabric.client.utils.wrappers.*;

public class render {

    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private static List<String> playerList = new ArrayList<>();
    private static final int MARGIN = 10;

    private static int customX = config.getInstance().getCustomX();
    private static int customY = config.getInstance().getCustomY();
    private static WidgetPosition widgetPosition = config.getInstance().getWidgetPosition();
    private nearby engine = new nearby();

    public void renderOverlay(DrawContext drawContext, MinecraftClient client) {
        if (client.options.hudHidden) {
            return;
        }

        if (client.world == null || client.player == null) {
            return;
        }

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

        if (!hasPlayersInChunks) {
            playerList.clear();
            playerList.add("No players nearby");
        }
        else if (!hasNearbyPlayers) {
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
                int color = line.equals("No players nearby") ? 0xFF6B6B : 0xFFFFFF; // Red for no players, white for player info
                drawContext.drawText(textRenderer, line, x + MARGIN, textY, color, false);
                textY += entryHeight;
            }
        }
    }

    private boolean hasPlayersInRenderedChunks(MinecraftClient client) {
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