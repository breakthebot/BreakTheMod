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

package net.charisk.breakthemod.fabric.client.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.charisk.breakthemod.Fetch.types.Nation;
import net.charisk.breakthemod.Fetch.types.Resident;
import net.charisk.breakthemod.Services.bestdeals;
import net.charisk.breakthemod.api.Fetch;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BestDeals extends FabricCommand{
    private final bestdeals Service = new bestdeals();

    @Override
    public String getName() {
        return "bestdeals";
    }

    @Override
    public String getDescription() {
        return "Shows you the best deals.";
    }

    @Override
    public String getUsageSuffix() {
        return "<name>";
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

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        String name = ctx.getArgument("name", String.class);
        MinecraftClient client = MinecraftClient.getInstance();

        CompletableFuture.supplyAsync(()-> {
            // Resident resident = new Fetch().getResident(client.getSession().getUsername());

            return Service.get(name,"United_states");
        }).thenAccept(
                out->sendMessage(MinecraftClient.getInstance(), Text.literal(
                        out.stream().toString()
                ))
        );

        return 0;
    }
}
