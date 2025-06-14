package net.charisk.breakthemod.fabric.client;
import net.charisk.breakthemod.fabric.client.commands.*;
import net.charisk.breakthemod.fabric.client.utils.render;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class breakthemodFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        help HelpCommand = new help();

        List<FabricCommand> commands = List.of(
                new GoTo(),
                new onlinestaff(),
                new friends(),
                new nationpop(),
                new locate(),
                new nearbyCommand(),
                new townless(),
                new whereIs(),
                new lastSeen(),
                HelpCommand
        );
        HelpCommand.setCommands(commands);
        loadCommands(commands);

        render Render = new render();
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            Render.renderOverlay(drawContext, MinecraftClient.getInstance());
        });
    }


    private void loadCommands(List<FabricCommand>  commands){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher,phase)->{
            for (FabricCommand command : commands){
                command.register(dispatcher);
            }
        });

    }
}
