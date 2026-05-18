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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Bar.class)
public interface ExperienceTextMixin {

    @Redirect(
            method = "drawExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"
            )
    )
    private static void drawScaledText(
            DrawContext context,
            TextRenderer renderer,
            Text text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        if (!Config.INSTANCE.getFeatures().getExperienceText()) {
            context.drawText(renderer, text, x, y, color, shadow);
            return;
        }
        context.getMatrices().pushMatrix();

        context.drawText(
                renderer,
                text,
                x,
                y,
                color,
                false
        );

        context.getMatrices().popMatrix();
    }

    @Redirect(
            method = "drawExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"
            )
    )
    private static MutableText redirectLevelText(
            String key,
            Object[] args
    ) {
        Objects.requireNonNull(MinecraftClient.getInstance().player);
        int level = (int) args[0];
        if (!Config.INSTANCE.getFeatures().getExperienceText()) return Text.translatable("gui.experience.level", level);
        return Text.literal(level + "(" +  ExperienceUtils.INSTANCE.experience(MinecraftClient.getInstance().player) + ")");
    }

}