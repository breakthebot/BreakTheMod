package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command as command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.types.Resident
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.chariskar.breakthemod.client.utils.Timestamps
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class lastSeen : Command() {
    init {
        name = "lastSeen"
        description = "Shows the last time a user was online"
        usageSuffix = "<name>"
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val player: Resident? = fetch.getResident(name)
            if (player == null) {
                sendMessage(Text.literal("Unable to find $name"), Formatting.RED)
                return@launch
            }
            val timestamps: List<Long> = Timestamps.parseTimestamp(player.timestamps?.lastOnline!!)
            if (player.status?.isOnline == true) {
                sendMessage(Text.literal("${player.name} has been online right now, for ${timestamps[0]} days, ${timestamps[1]} hours and ${timestamps[2]} minutes."), Formatting.AQUA)
            } else {
                sendMessage(Text.literal("${player.name} was last online ${timestamps[0]} days, ${timestamps[1]} hours and ${timestamps[2]} minutes."), Formatting.AQUA)
            }
            return@launch
        }
        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                        .executes(command { context: CommandContext<FabricClientCommandSource> ->
                            if (!getEnabled()) return@command 0
                            return@command run(context)
                        })
                )
        )
    }
}