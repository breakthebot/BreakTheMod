package net.chariskar.breakthemod.fabric;

import net.chariskar.breakthemod.breakthemod;
import net.chariskar.breakthemod.utils.config;
import net.fabricmc.api.ModInitializer;

public final class BreakthemodFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        breakthemod.init();

    }
}
