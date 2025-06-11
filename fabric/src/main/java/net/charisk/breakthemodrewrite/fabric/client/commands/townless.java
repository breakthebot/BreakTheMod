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
import com.mojang.brigadier.context.CommandContext;
import net.charisk.breakthemodrewrite.Fetch.types.Resident;
import net.charisk.breakthemodrewrite.api.Fetch;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.charisk.breakthemodrewrite.Services.townlessService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class townless extends FabricCommand{
    private townlessService Service = new townlessService();


    @Override
    public String getName() {
        return "townless";
    }

    @Override
    public String getDescription() {
        return "Shows all online townless users.";
    }

    @Override
    public String getUsageSuffix() {
        return "";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        String playerName = client
                .getSession()
                .getUsername();
        CompletableFuture.supplyAsync(()->Service.get(client.getNetworkHandler().getPlayerList().stream()
                .map(e -> e.getProfile().getName())
                .collect(Collectors.toList()))).thenAccept(
                (townless)->{
                    MutableText message = Text.literal("Townless Users:\n").setStyle(Style.EMPTY.withColor(Formatting.AQUA));
                    Resident own = new Fetch().getResident(playerName);

                    for (String user : townless) {
                        String inviteMessage = "/msg " + user + " Hi! I see you're new here, wanna join my Town? I can help you out! Get Free enchanted Armor, Pickaxe, Diamonds, Iron, wood, food, stone, house, and ability to teleport! Type /t join " + own.getTown().get().getName().get();

                        Text userText = Text.literal(user)
                                .setStyle(Style.EMPTY
                                        .withColor(Formatting.GREEN)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, inviteMessage))
                                        .withHoverEvent(new net.minecraft.text.HoverEvent(
                                                net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                                                Text.literal("Click to copy invite message for " + user)
                                        ))
                                );

                        message.append(userText).append(Text.literal("\n"));
                    }

                    sendMessage(client, message);
                }
        );
        return 0;
    }

}
