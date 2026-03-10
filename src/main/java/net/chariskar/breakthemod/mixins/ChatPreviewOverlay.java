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

import net.chariskar.breakthemod.client.modules.ChatPreview;
import net.chariskar.breakthemod.client.utils.ChatChannel;
import net.chariskar.breakthemod.client.utils.ServerUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

///  Credit to [...](https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/mixin/EditBoxMixin.java)

@Mixin(TextFieldWidget.class)
public abstract class ChatPreviewOverlay extends ClickableWidget {

    @Shadow @Final private TextRenderer textRenderer;
    @Shadow @Nullable private String suggestion;
    @Shadow private String text;

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");

    protected ChatPreviewOverlay(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void inject(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        try {
            if (!ServerUtils.INSTANCE.isEarthMc()) return;
            if (!getMessage().equals(Text.translatable("chat.editBox"))) return;

            if (this.suggestion != null || !this.text.isEmpty()) return;

            ChatChannel current = ChatPreview.INSTANCE.getChatChannel();
            if (current == null) return;

            boolean inParty = ChatPreview.INSTANCE.getInPartyChat();
            String label = inParty ? current.getName() + " (party)" : current.getName();

            int x = this.getX();
            int y = this.getY();

            context.drawText(
                    this.textRenderer,
                    label,
                    x,
                    y,
                    current.getColour(),
                    false
            );

        } catch (Exception e) {
            LOGGER.warn("{}", e.getMessage());
        }
    }
}