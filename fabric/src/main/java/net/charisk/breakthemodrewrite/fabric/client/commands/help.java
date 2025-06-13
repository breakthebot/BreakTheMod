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


import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mutable;

import java.util.List;

public class help extends FabricCommand{
    public List<FabricCommand> commands = null;

    public void setCommands(List<FabricCommand> Commands) {commands = Commands;}

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "The help command";
    }

    @Override
    public String getUsageSuffix() {
        return "";
    }


    @Override
    protected int execute(CommandContext<FabricClientCommandSource> ctx) throws Exception {
        MinecraftClient client = ctx.getSource().getClient();

        if (commands == null || commands.isEmpty()) {
            sendMessage(client, Text.literal("No commands available.").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            return 1;
        }

        sendMessage(client, Text.literal("=== Available Commands ===").setStyle(Style.EMPTY.withColor(Formatting.GOLD)));

        for (FabricCommand cmd : commands) {
            MutableText cmdNameText = Text.literal("/" + cmd.getName())
                    .formatted(Formatting.GRAY);

            if (!cmd.getUsageSuffix().isEmpty()) {
                cmdNameText = cmdNameText.append(
                        Text.literal(" " + cmd.getUsageSuffix())
                );
            }

            Text descText = Text.literal(" - " + cmd.getDescription())
                    .formatted(Formatting.WHITE);

            sendMessage(client, cmdNameText.append(descText));
        }

        return 1;
    }
}
