package net.charisk.breakthemod.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import net.charisk.breakthemod.breakthemod;
import net.charisk.breakthemod.neoforge.commands.GoTo;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


@Mod(breakthemod.MOD_ID)
public final class breakthemodNeoForge {
    public breakthemodNeoForge() {
        breakthemod.init();
        // Register the command registration method to the NeoForge event bus
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        new GoTo().register(dispatcher);
        // Register additional commands here
    }
}