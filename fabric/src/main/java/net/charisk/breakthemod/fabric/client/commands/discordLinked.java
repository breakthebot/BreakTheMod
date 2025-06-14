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
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.charisk.breakthemod.Services.discordLinkedService;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class discordLinked extends FabricCommand{
    private discordLinkedService Service = new discordLinkedService();

    @Override
    public String getName() {
        return "discordlinked";
    }

    @Override
    public String getDescription() {
        return "It tells you the discord username of a linked player";
    }

    @Override
    public String getUsageSuffix() {
        return "<name>";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        String name = ctx.getArgument("name", String.class);

        client.execute(()->{
            String resp = Service.get(name);
            if (resp.equalsIgnoreCase("null")){
                sendMessage(client, Text.literal("No Discord ID linked with the provided Minecraft username."));
            }
            Text result = Text.literal("Click Here")
                    .setStyle(Style.EMPTY
                            .withColor(Formatting.BLUE)
                            .withClickEvent(
                                    new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/users/" + resp)
                            )

                    );
            sendMessage(client, result);
        });
        return 0;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal(getName())
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    if (!getEnabledOnOtherServers()) return 0;
                                    return run(context);
                                })
                        )
        );
    }
}
