package net.charisk.breakthemodrewrite.fabric.client;
import net.charisk.breakthemodrewrite.fabric.client.commands.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public final class BreakthemodrewriteFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        loadCommands(
                new GoTo(),
                new onlinestaff(),
                new friends(),
                new nationpop(),
                new locate()
        );
    }


    private void loadCommands(FabricCommand... commands){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher,phase)->{
            for (FabricCommand command : commands){
                command.register(dispatcher);
            }
        });

    }
}
