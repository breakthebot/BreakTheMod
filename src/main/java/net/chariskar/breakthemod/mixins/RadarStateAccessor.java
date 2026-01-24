package net.chariskar.breakthemod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import xaero.hud.minimap.radar.state.RadarList;
import xaero.hud.minimap.radar.state.RadarState;

import java.util.List;

@Mixin(RadarState.class)
public interface RadarStateAccessor {

    @Invoker("getUpdatableLists")
    List<RadarList> callGetUpdatableLists();
}

