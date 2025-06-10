package net.charisk.breakthemodrewrite.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import net.charisk.breakthemodrewrite.Breakthemodrewrite;
import net.charisk.breakthemodrewrite.neoforge.commands.GoTo;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


@Mod(Breakthemodrewrite.MOD_ID)
public final class BreakthemodrewriteNeoForge {
    public BreakthemodrewriteNeoForge() {
        Breakthemodrewrite.init();
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