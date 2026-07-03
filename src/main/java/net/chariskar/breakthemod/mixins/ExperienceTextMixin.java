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

import net.chariskar.breakthemod.client.utils.Config;
import net.chariskar.breakthemod.client.utils.ExperienceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ContextualBar.class)
public interface ExperienceTextMixin {

    @Redirect(
            method = "extractExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"
            )
    )
    private static void drawScaledText(
            GuiGraphicsExtractor instance,
            Font font,
            Component str,
            int x,
            int y,
            int color,
            boolean dropShadow
    ) {
        if (!Config.INSTANCE.getFeatures().getExperienceComponent()) {
            instance.text(font, str, x, y, color, dropShadow);
            return;
        }
        instance.pose().pushMatrix();

        instance.text(
                font,
                str,
                x,
                y,
                color,
                false
        );


        instance.pose().popMatrix();
    }

    @Redirect(
            method = "extractExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private static MutableComponent redirectLevelText(
            String key,
            Object[] args
    ) {
        Objects.requireNonNull(Minecraft.getInstance().player);
        int level = (int) args[0];
        if (!Config.INSTANCE.getFeatures().getExperienceComponent())
            return Component.translatable("gui.experience.level", level);
        return Component.literal(level + "(" + ExperienceUtils.INSTANCE.experience(Minecraft.getInstance().player) + ")");
    }

}