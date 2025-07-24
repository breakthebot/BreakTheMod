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
import net.chariskar.breakthemod.Services.locateService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;

public class locate extends FabricCommand{
    locateService Service = new locateService();

    @Override
    public String getName() {
        return "locate";
    }

    @Override
    public String getDescription() {
        return "Gives the spawn coordinates of a town or a nation";
    }

    @Override
    public String getUsageSuffix() {
        return "<name> <type>";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx)  {
        String name = ctx.getArgument("name", String.class);
        String type = ctx.getArgument("type", String.class).toLowerCase();
        CompletableFuture.supplyAsync(() -> Service.getLocation(name, locateService.LocationType.fromString(type)))
                .thenAccept(result -> {
                    if (result == null) {
                        client.execute(() -> sendMessage(client, Text.literal("Location not found or error occurred.")
                                .setStyle(Style.EMPTY.withColor(Formatting.RED))));
                    } else {
                        Text message = Text.literal(String.format("%s is located at X: %d, Z: %d. ",
                                        result.getName(), result.getX(), result.getZ()))
                                .append(Text.literal("Click Here")
                                        .styled(style -> style.withColor(Formatting.AQUA)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, result.getMapUrl()))));

                        client.execute(() -> sendMessage(client, message));
                    }
                });
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.string())
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("type", StringArgumentType.string())
                                        .executes(context -> {
                                            if (!getEnabled()) return 0;
                                            return run(context);
                                        })
                                )
                        )
        );
    }
}
