package net.charisk.breakthemod.neoforge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.charisk.breakthemod.api.Command;
import net.charisk.breakthemod.utils.config;
import net.charisk.breakthemod.neoforge.Prefix;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;

public abstract class NeoForgeCommand extends Command<CommandSourceStack> {


    public static String getConnectedServerAddress() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return null;

        ServerData serverData = client.getCurrentServer();
        return serverData != null ? serverData.ip : null;
    }

    public static boolean getEnabledOnOtherServers() {
        String serverAddress = getConnectedServerAddress();
        if (serverAddress == null) return true;
        if (serverAddress.toLowerCase().endsWith("earthmc.net")) return true;
        return config.getInstance().isEnabledOnOtherServers();
    }

    public void sendMessage(Minecraft client, Component message) {
        if (client.player != null) {
            Component prefix = Prefix.getPrefix();
            Component chatMessage = prefix.copy().append(message);
            client.player.displayClientMessage(chatMessage, false);
        }
    }

    @Override
    protected int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        try {
            return execute(ctx);
        } catch (CommandSyntaxException e) {
            throw e;
        } catch (Exception e) {
            Minecraft.getInstance().execute(() -> {
                if (Minecraft.getInstance().player != null) {
                    sendMessage(Minecraft.getInstance(), Component.literal("Unexpected error: " + e.getMessage()));
                }
            });
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(getName())
                        .executes(context -> {
                            if (!getEnabledOnOtherServers()) return 0;
                            return run(context);
                        })

        );
    }

    @Override
    protected abstract int execute(CommandContext<CommandSourceStack> ctx) throws Exception;
}
