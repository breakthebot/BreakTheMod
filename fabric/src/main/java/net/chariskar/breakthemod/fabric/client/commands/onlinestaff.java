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


import com.mojang.brigadier.context.CommandContext;
import net.chariskar.breakthemod.Services.onlinestaffService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class onlinestaff extends FabricCommand{
    private final onlinestaffService Service = new onlinestaffService();

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
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        CompletableFuture.supplyAsync(()-> {
            try {
                return Service.get(client.getNetworkHandler().getPlayerList().stream()
                        .map(e -> e.getProfile().getId())
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                LOGGER.error("Failed to fetch online players, error {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }).thenAccept((resp)->{
            List<String> onlineStaffNames = client.getNetworkHandler().getPlayerList().stream()
                    .filter(e -> resp.contains(e.getProfile().getId()))
                    .map(e -> e.getProfile().getName())
                    .collect(Collectors.toList());

            if (!onlineStaffNames.isEmpty()) {
                MutableText onlineStaffText = Text.literal("");

                for (int i = 0; i < onlineStaffNames.size(); i++) {
                    onlineStaffText = onlineStaffText.append(
                            Text.literal(onlineStaffNames.get(i)).setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                    );

                    if (i < onlineStaffNames.size() - 1) {
                        onlineStaffText = onlineStaffText.append(
                                Text.literal(", ").setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                        );
                    }
                }

                Text message = Text.literal("")
                        .append(onlineStaffText)
                        .append(Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                        .append(Text.literal(String.valueOf(onlineStaffNames.size())).setStyle(Style.EMPTY.withColor(Formatting.WHITE)))
                        .append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));

                sendMessage(client, message);

            } else {
                sendMessage(client, Text.literal("No staff online").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            }
        });
        return 0;
    }

}
