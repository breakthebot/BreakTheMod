package net.charisk.breakthemodrewrite.fabric.client;
import net.charisk.breakthemodrewrite.fabric.client.commands.*;
import net.charisk.breakthemodrewrite.fabric.client.utils.render;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.List;

public final class BreakthemodrewriteFabricClient implements ClientModInitializer {
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
