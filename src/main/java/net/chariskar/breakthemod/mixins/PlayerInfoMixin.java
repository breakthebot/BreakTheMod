/*
 * This file is part of breakthemod.
 *
 * breakthemod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemod. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider;
import net.chariskar.breakthemod.client.modules.Cache;
import net.chariskar.breakthemod.client.modules.CacheKt;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.breakthebot.breakthelibrary.models.Resident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Credit to [...](https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/mixin/PlayerRendererMixin.java)

@Mixin(AvatarRenderer.class)
public abstract class PlayerInfoMixin extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> implements ServerUtilsProvider {

    public PlayerInfoMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }


    @Inject(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD")
    )
    private void inject(
            AvatarRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo ci) {
        if (!isEarthMc()) return;

        String nameString = state.nameTag != null ? state.nameTag.getString() : "";

        Resident cachedPlayer = Cache.INSTANCE.getPlayer(nameString);
        if (cachedPlayer == null) return;

        Component townyText = CacheKt.getTownyComponent(cachedPlayer);
        poseStack.translate(0D, 0.12225D, 0D);

        poseStack.pushPose();

        poseStack.scale(0.75F, 0.75F, 0.75F);

        poseStack.translate(0.0D, 2.25F, 0.0D);

        submitNodeCollector.submitNameTag(
                poseStack,
                new Vec3(0, 0, 0),
                0,
                townyText,
                !state.isCrouching,
                state.lightCoords,
                camera
        );
        poseStack.popPose();

    }
}