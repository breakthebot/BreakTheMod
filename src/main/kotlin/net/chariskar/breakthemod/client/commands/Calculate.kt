package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.Locale
import java.util.concurrent.CompletableFuture

class Calculate : BaseCommand() {
    init {
        name = "calculate"
        description = "Helps with the conversions of blocks & stacks."
        usageSuffix = "<type> <amount>"
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val type = ctx.getArgument("type", String::class.java)
        val amount = ctx.getArgument("amount", Int::class.java)
        return when ( type ) {
            "blocks" -> {
                val fullBlocks = amount / 9
                val remainder = amount % 9

                sendMessage(Text.literal("$amount gold ingots equal $fullBlocks blocks and $remainder ingots."))
                0
            }
            "stacks" -> {
                val fullStacks = amount / 64
                val remainder = amount % 64

                sendMessage(Text.literal("$amount blocks are $fullStacks stacks and $remainder blocks"))
                0
            }
            else -> {
                sendMessage(Text.literal("$type is not blocks or stacks"), Formatting.RED)
                1
            }
        }
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("type", StringArgumentType.string())
                    .suggests(CalculateSuggestion())
                    .then(
                        RequiredArgumentBuilder.argument<FabricClientCommandSource?, Int>("amount", IntegerArgumentType.integer())
                            .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                                if (!getEnabled()) return@Command 0
                                return@Command run(context)
                            })
                    )

                )
        )
    }

    class CalculateSuggestion : SuggestionProvider<FabricClientCommandSource?> {
        val allSuggestions: MutableList<String> = mutableListOf("blocks", "stacks")

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            allSuggestions.stream()
                .filter { s -> s?.startsWith(input) == true }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}