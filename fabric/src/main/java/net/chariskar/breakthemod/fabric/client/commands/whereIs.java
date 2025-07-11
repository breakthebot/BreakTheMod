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
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.chariskar.breaktheapi.Services.findPlayerService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class whereIs extends FabricCommand{
    findPlayerService Service = new findPlayerService();

    @Override
    public String getName() {
        return "findplayer";
    }

    @Override
    public String getDescription() {
        return "Tells you where a player is based on the map api.";
    }

    @Override
    public String getUsageSuffix() {
        return "<name>";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        String name = ctx.getArgument("name", String.class);
        MinecraftClient client = MinecraftClient.getInstance();
        CompletableFuture.supplyAsync(()->Service.get(name)).thenAccept(
                (resp)->{
                    if (resp.get().found) sendMessage(client,Text.literal(resp.get().toString()));
                    else sendMessage(client, Text.literal(resp.get().toString()).setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
                }
        );
        return 0;
    }

    @Override
    public void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.string()).executes(context -> {
                            if (!getEnabledOnOtherServers()) return 0;
                            return run(context);
                        }))
        );
    }
}
