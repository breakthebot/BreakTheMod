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

import net.chariskar.breakthemod.client.modules.Cache;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.breakthebot.breakthelibrary.models.Resident;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Credit to [...](https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/mixin/PlayerRendererMixin.java)

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerInfoMixin {

    @Inject(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At("TAIL")
    )
    private void inject(
            PlayerEntityRenderState state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraRenderState,
            CallbackInfo ci
    ) {
       // if (!ServerUtils.INSTANCE.isEarthMc()) return;

        String nameString = state.displayName != null ? state.displayName.getString() : "";

        Resident cachedPlayer = Cache.INSTANCE.getCachedPlayers().stream()
                .filter(current -> current.getName().equals(nameString))
                .findFirst()
                .orElse(null);

        if (cachedPlayer == null) return;

        Text townyText = createTownyText(cachedPlayer);
        matrices.push();

        matrices.scale(0.75F, 0.75F, 0.75F);

        Vec3d labelPos = state.nameLabelPos.add(0, 2.2, 0);
        matrices.translate(
                labelPos.x - state.x,
                labelPos.y - state.y,
                labelPos.z - state.z
        );

        queue.submitLabel(
                matrices,
                new Vec3d(0,0,0),
                0,
                townyText,
                !state.sneaking,
                state.light,
                state.squaredDistanceToCamera,
                cameraRenderState
        );
    }

    @Unique
    private Text createTownyText(@NotNull Resident player) {
        if (player.getStatus() == null) return Text.empty();

        if (!Boolean.TRUE.equals(player.getStatus().getHasTown())) {
            return Text.literal("Nomad").formatted(Formatting.DARK_AQUA);
        }

        StringBuilder builder = new StringBuilder();
        if (Boolean.TRUE.equals(player.getStatus().isMayor())) {
            builder.append("👑 ");
        }

        builder.append("[");
        if (Boolean.TRUE.equals(player.getStatus().getHasNation())) {
            builder.append(player.getNation().getName()).append("|");
        }
        builder.append(player.getTown().getName()).append("]");

        return Text.literal(builder.toString())
                .formatted(Formatting.DARK_AQUA);
    }

}