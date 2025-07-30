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

package net.chariskar.breakthemod.fabric.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.chariskar.breakthemod.api.Command;
import net.chariskar.breakthemod.fabric.client.Prefix;
import net.chariskar.breakthemod.utils.config;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public abstract class FabricCommand extends Command<FabricClientCommandSource> {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    public static String getConnectedServerAddress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null) return null;

        return serverInfo.address.split(",")[0];
    }

    public static boolean getEnabled() {
        String serverAddress = getConnectedServerAddress();

        if (serverAddress == null) {return true;}

        if (serverAddress.toLowerCase().contains("earthmc.net")) return true;

        return config.getInstance().isEnabledOnOtherServers();
    }

    /**
     * Helper utility for sending messages.
     *
     * @param client  The minecraft client instance
     * @param message The message to be sent
     */
    public void sendMessage(MinecraftClient client, Text message) {
        client.execute(() -> {
            if (client.player != null) {
                Text prefix = Prefix.getPrefix();
                Text chatMessage = Text.literal("").append(prefix).append(message);
                client.player.sendMessage(chatMessage, false);
            }
        });
    }

    protected abstract int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception;

    protected int run(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        try {
            return execute(ctx);
        } catch (CommandSyntaxException e) {
            throw e;
        } catch (Exception e) {
            MinecraftClient.getInstance().execute(() -> {
                if (MinecraftClient.getInstance().player != null) {
                    sendMessage(MinecraftClient.getInstance(),Text.empty().append("Unexpected error: " + e.getMessage()));
                }
            });
            logError("Unexpected error has occurred while running " + getName(), e);
            return 0;
        }
    }

    @Override
    public void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .executes(context -> {
                            if (!getEnabled()) return 0;
                            return run(context);
                        })

        );
    }
}
