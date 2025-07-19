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
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.chariskar.breakthemod.Services.GoToService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GoTo extends FabricCommand{
    private final GoToService Service = new GoToService();

    @Override
    public String getName() {
        return "goto";
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
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("destination", StringArgumentType.string())
                                .executes(context -> {
                                    if (!getEnabledOnOtherServers()) return 0;
                                    return run(context);
                                })
                        )
        );
    }

    private static final SuggestionProvider<FabricClientCommandSource> NAME_SUGGESTIONS = (context, builder) -> {
        List<String> names = List.of("Town", "Nation");
        for (String name : names) {
            if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    };

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        String destination = ctx.getArgument("destination", String.class);
        MinecraftClient client = MinecraftClient.getInstance();
        if (!getEnabledOnOtherServers()) return 0;

        Service.findValidTowns(destination)
                .thenAcceptAsync(output -> {
                    client.execute(() -> {
                        if (output.isEmpty()) {
                            sendMessage(client,
                                    Text.literal("No suitable spawns found")
                                            .setStyle(Style.EMPTY.withColor(Formatting.RED))
                            );
                        } else {
                            sendMessage(client,
                                    Text.literal("Found suitable spawn for " + destination + " in: " + String.join(", ", output))
                                            .setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                            );
                        }
                    });
                }, client)
                .exceptionally(ex -> {
                    client.execute(() -> sendMessage(client,
                            Text.literal("Error finding towns: " + ex.getMessage())
                                    .setStyle(Style.EMPTY.withColor(Formatting.RED))
                    ));
                    return null;
                });

        return 1;
    }
}
