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
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.charisk.breakthemodrewrite.commands.onlinestaffService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class onlinestaff extends FabricCommand{
    private onlinestaffService Service = new onlinestaffService();

    @Override
    public String getName() {
        return "onlinestaff";
    }

    @Override
    public String getDescription() {
        return "Shows all online staff";
    }

    @Override
    public String getUsageSuffix() {
        return "";
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal("onlinestaff")
                        .executes(context -> {
                            if (!getEnabledOnOtherServers()) return 0;
                            return run(context);
                        })

        );
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            try {
                List<UUID> onlineUuids = client.getNetworkHandler().getPlayerList().stream()
                        .map(e -> e.getProfile().getId())
                        .collect(Collectors.toList());

                List<UUID> onlineStaffUuids = Service.get(onlineUuids);

                List<String> onlineStaffNames = client.getNetworkHandler().getPlayerList().stream()
                        .filter(e -> onlineStaffUuids.contains(e.getProfile().getId()))
                        .map(e -> e.getProfile().getName())
                        .collect(Collectors.toList());

                if (!onlineStaffNames.isEmpty()) {
                    Text styledPart = Text.literal("Online Staff: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA));
                    Text onlineStaffText = Text.literal(String.join(", ", onlineStaffNames))
                            .setStyle(Style.EMPTY.withColor(Formatting.GREEN));
                    Text message = Text.literal("")
                            .append(styledPart)
                            .append(onlineStaffText)
                            .append(Text.literal(" [" + onlineStaffNames.size() + "]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));

                    sendMessage(client, message);
                } else {
                    sendMessage(client, Text.literal("No staff online").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(client, Text.literal("Error while checking staff online").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            }
        });
        return 0;
    }

}
