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
import net.chariskar.breaktheapi.engine.nearby;
import net.chariskar.breakthemod.fabric.client.utils.wrappers.player;
import net.chariskar.breakthemod.fabric.client.utils.wrappers.world;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Set;

public class nearbyCommand extends FabricCommand{
    nearby Engine = new nearby();

    @Override
    public String getName() {
        return "nearby";
    }

    @Override
    public String getDescription() {
        return "Shows all nearby players as they would be shown on the map";
    }

    @Override
    public String getUsageSuffix() {
        return "";
    }

    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        player Playerwrapper = new player(client.player);
        world Worldwrapper = new world(client.world);

        Set<String> nearbyPlayers = Engine.updateNearbyPlayers(Playerwrapper, Worldwrapper);
        if (nearbyPlayers.isEmpty()) {
            client.execute(() -> sendMessage(client, Text.literal("There are no players nearby").setStyle(Style.EMPTY.withColor(Formatting.RED))));
            return 0;
        }

        MutableText header = Text.literal("Players nearby:\n").setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
        MutableText playersText = Text.literal("");

        for (String playerInfo : nearbyPlayers) {
            playersText.append(Text.literal(playerInfo + "\n").setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
        }

        client.execute(() -> sendMessage(client, header.append(playersText)));
        return 0;

    }

}
