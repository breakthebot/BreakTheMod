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

package net.charisk.breakthemod.neoforge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.charisk.breakthemod.Services.GoToService;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Style;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GoTo extends NeoForgeCommand{
    private final GoToService Service = new GoToService();

    @Override
    public String getName() {
        return "GoTo";
    }

    @Override
    public String getDescription() {
        return "Tells you in which town to spawn in so you are the closest to the town you want to go to";
    }

    @Override
    public String getUsageSuffix() {
        return "<destination>";
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(getName())
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("destination", StringArgumentType.string())
                                .executes(context -> {
                                    if (!getEnabledOnOtherServers()) return 0;
                                    return run(context);
                                })
                        )
        );
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx) throws Exception {
        if (!getEnabledOnOtherServers()) return 0;
        String destination = ctx.getArgument("destination", String.class);
        Minecraft client = Minecraft.getInstance();

        Service.findValidTowns(destination)
                .thenAcceptAsync(output -> {
                    client.execute(() -> {
                        if (output.isEmpty()) {
                            sendMessage(client,
                                    Component.literal("No suitable spawns found")
                                            .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                            );
                        } else {
                            sendMessage(client,
                                    Component.literal("Found suitable spawn in: " + String.join(", ", output))
                                            .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))
                            );
                        }
                    });
                }, client);

        return 1;
    }
}
