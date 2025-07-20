package net.chariskar.breakthemod.fabric.client;


import net.chariskar.breakthemod.fabric.client.commands.*;
import net.chariskar.breakthemod.fabric.client.commands.econ.calculateGold;
import net.chariskar.breakthemod.fabric.client.commands.econ.calculateStacks;
import net.chariskar.breakthemod.fabric.client.utils.render;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.logging.Logger;

public final class breakthemodFabricClient implements ClientModInitializer {
    private static final Identifier NEARBY_LAYER = Identifier.of("breakthemod", "nearby_layer");
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
                new discordLinked(),
                new coords(),
                new calculateGold(),
                new calculateStacks(),
                HelpCommand
        );
        HelpCommand.setCommands(commands);
        loadCommands(commands);

        render Render = new render();
        LayeredDrawer drawer = new LayeredDrawer();

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, NEARBY_LAYER, render::renderOverlay));
    }


    private void loadCommands(List<FabricCommand>  commands){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher,phase)->{
            for (FabricCommand command : commands){
                command.register(dispatcher);
            }
        });

    }
}
