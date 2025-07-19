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
import net.chariskar.breakthemod.Services.friendsService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class friends extends FabricCommand {
    private final friendsService service = new friendsService();

    @Override
    public String getName() {
        return "onlinefriends";
    }

    @Override
    public String getDescription() {
        return "Tells you which of your friends are online";
    }

    @Override
    public String getUsageSuffix() {
        return "";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!getEnabledOnOtherServers()) return 0;
        assert client.player != null;


        String userID = client
                .getSession()
                .getUsername();

        List<String> knownFriends = service.get(userID);

        List<String> onlinePlayerNames = MinecraftClient.getInstance()
                .getNetworkHandler()
                .getPlayerList()
                .stream()
                .map(entry -> entry.getProfile().getName())
                .toList();

        List<String> onlineFriends = onlinePlayerNames.stream()
                .filter(knownFriends::contains)
                .toList();
        if (onlineFriends.isEmpty()) {
            sendMessage(client, Text.literal("No friends online").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        } else {
            Text styledPart = Text.literal("Online Friends: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA));
            Text onlineFriendsText = Text.literal(String.join(", ", onlineFriends))
                    .setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            Text message = Text.literal("")
                    .append(styledPart)
                    .append(onlineFriendsText)
                    .append(" [" + onlineFriends.size() + "]");
            sendMessage(client, message);
        }
        return 1;
    }

}
