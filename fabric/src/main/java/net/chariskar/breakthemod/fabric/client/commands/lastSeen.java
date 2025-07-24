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
import net.chariskar.breakthemod.Services.lastSeenService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;


public class lastSeen extends FabricCommand {

    lastSeenService Service = new lastSeenService();

    @Override
    public String getName() {
        return "lastseen";
    }

    @Override
    public String getDescription() {
        return "Tells you when a person was last online";
    }

    @Override
    public String getUsageSuffix() {
        return "<name>";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        String name = ctx.getArgument("name", String.class);

        // NOTE: client.exec is used here as this isnt a resource heavy task.
        client.execute(()->{
            String resp = Service.get(name);
            sendMessage(client, Text.literal(resp));
        });
        return 0;
    }


    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    if (!getEnabled()) return 0;
                                    return run(context);
                                })
                        )
        );
    }
}
