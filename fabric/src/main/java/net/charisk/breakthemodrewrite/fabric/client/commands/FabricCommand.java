/*
 * This file is part of breakthemodrewrite.
 *
 * breakthemodrewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodrewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodrewrite. If not, see <https://www.gnu.org/licenses/>.
 */

package net.charisk.breakthemodrewrite.fabric.client.commands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.charisk.breakthemodrewrite.api.Command;
import net.charisk.breakthemodrewrite.utils.config;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.charisk.breakthemodrewrite.fabric.client.Prefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FabricCommand extends Command<FabricClientCommandSource> {
    public static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");

    public String getConnectedServerAddress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;

        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null) return null;

        return serverInfo.address.split(",")[0];
    }

    public boolean getEnabledOnOtherServers() {
        String serverAddress = getConnectedServerAddress();

        if (serverAddress == null) {return true;}

        if (serverAddress.toLowerCase().endsWith("earthmc.net")) return true;

        return config.getInstance().isEnabledOnOtherServers();
    }

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
            e.printStackTrace();
            return 0;
        }
    }


    public abstract void register(CommandDispatcher<FabricClientCommandSource> dispatcher);
}
