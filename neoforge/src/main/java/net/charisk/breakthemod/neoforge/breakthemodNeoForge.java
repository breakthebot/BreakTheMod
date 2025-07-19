package net.charisk.breakthemod.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.platform.Platform;
import net.charisk.breakthemod.breakthemod;
import net.charisk.breakthemod.breakthemod;
import net.charisk.breakthemod.neoforge.commands.GoTo;
import net.charisk.breakthemod.neoforge.commands.NeoForgeCommand;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.File;
import java.util.List;


@Mod(breakthemod.MOD_ID)
public final class breakthemodNeoForge {
    public breakthemodNeoForge() {

        breakthemod.init();
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        List<NeoForgeCommand> commands = List.of(
                new GoTo()
        );

        registerCommands(dispatcher, commands);
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, List<NeoForgeCommand> commands) {
        for (NeoForgeCommand command : commands){
            command.register(dispatcher);
        }
    }
}