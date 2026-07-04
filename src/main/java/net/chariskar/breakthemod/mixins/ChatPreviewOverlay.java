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

import net.chariskar.breakthemod.Breakthemod;
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider;
import net.chariskar.breakthemod.client.models.ChatChannel;
import net.chariskar.breakthemod.client.modules.ChatTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

///  Credit to [...](https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/mixin/EditBoxMixin.java)
///
@Mixin(EditBox.class)
public abstract class ChatPreviewOverlay extends AbstractWidget implements ServerUtilsProvider {
    @Unique
    Logger logger = Breakthemod.Companion.getLogger();
    @Shadow
    @Final
    private Font font;
    @Shadow
    private @Nullable String suggestion;
    @Shadow
    private String value;

    public ChatPreviewOverlay(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(
            method = "extractWidgetRenderState",
            at = @At("TAIL")
    )
    private void inject(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        try {
            if (!isEarthMc()) return;
            if (!getMessage().equals(Component.translatable("chat.editBox"))) return;

            if (this.suggestion != null || !this.value.isEmpty()) return;

            ChatChannel current = ChatTracker.INSTANCE.getChatChannel();
            if (current == null) return;

            boolean inParty = ChatTracker.INSTANCE.getInPartyChat();
            String label = inParty ? current.getName() + " (party)" : current.getName();

            try {
                int x = this.getX();
                int y = this.getY();
                int color = current.getColour();
                graphics.text(this.font, label, x, y, color, false);
            } catch (Exception e) {
                logger.warn("[EditBoxMixin] Exception drawing string: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.warn("[EditBoxMixin] Exception in inject: {}", e.getMessage());
        }
    }
}