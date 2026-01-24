package net.chariskar.breakthemod.mixins;


import net.chariskar.breakthemod.client.utils.Config;
import net.chariskar.breakthemod.client.utils.ServerUtils;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.HudMod;

@Mixin(value = HudMod.class, remap = false)
public abstract class FairPlay {

    @Inject(method = "isFairPlay", at = @At("HEAD"), cancellable = true)
    private void inject(CallbackInfoReturnable<Boolean> cir) {
        if (!ServerUtils.INSTANCE.isEarthMc() && Config.INSTANCE.getXaerosRdr()) return;
        LoggerFactory.getLogger("breakthemod").info("Bypassed fairplay");
        cir.setReturnValue(false);
    }
}