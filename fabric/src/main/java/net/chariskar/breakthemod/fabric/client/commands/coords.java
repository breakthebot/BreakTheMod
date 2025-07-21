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
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.chariskar.breakthemod.Services.coordsService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class coords extends FabricCommand{
    coordsService Service = new coordsService();

    @Override
    public String getName() {
        return "coords";
    }

    @Override
    public String getDescription() {
        return "Tells you information about the coords.";
    }

    @Override
    public String getUsageSuffix() {
        return "<x> <z>";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        double x = ctx.getArgument("x", double.class);
        double z = ctx.getArgument("z", double.class);
        client.execute(()->{
            try {
                coordsService.LocationResult resp = Service.get(x,z);
                sendMessage(client, Text.literal(resp.format()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, Double>argument("x", DoubleArgumentType.doubleArg())
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, Double>argument("z", DoubleArgumentType.doubleArg())
                                    .executes(context -> {
                                        if (!getEnabledOnOtherServers()) return 0;
                                        return run(context);
                                    })
                        ))
        );
    }
}
