/*
 * This file is part of BreakTheMod.
 *
 * BreakTheMod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BreakTheMod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BreakTheMod. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.fabric.client.commands.econ;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.chariskar.breakthemod.fabric.client.commands.FabricCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class calculateGold extends FabricCommand {

    @Override
    public String getName() {
        return "calculateGold";
    }

    @Override
    public String getDescription() {
        return "Returns how many blocks the ingots are";
    }

    @Override
    public String getUsageSuffix() {
        return "<ingots>";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        return 0;
    }

    @Override
    public void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register( LiteralArgumentBuilder
                .<FabricClientCommandSource>literal("calculateGold")
                .then(RequiredArgumentBuilder
                        .<FabricClientCommandSource, Integer>argument("ingots", IntegerArgumentType.integer())
                        .executes(context -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (!getEnabled()) return 0;
                            int ingots = IntegerArgumentType.getInteger(context, "ingots");

                            int fullBlocks = ingots / 9;
                            int remainder = ingots % 9;

                            String message = ingots + " gold ingots equal "
                                    + fullBlocks + " blocks and " + remainder + " ingots";

                            client.execute(() -> sendMessage(client, Text.literal(message)));
                            return 1;
                        })
                )
        );
    }
}