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

import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider;
import net.chariskar.breakthemod.client.modules.ActionTracker;
import net.chariskar.breakthemod.client.widgets.FishingWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public class FishingEvent implements ServerUtilsProvider {

    @Inject(
            method = "pullHookedEntity",
            at = @At("TAIL")
    )
    public void pullHookedEntity(Entity entity, CallbackInfo ci) {
        if (!isModEnabled() || !FishingWidget.INSTANCE.getConfig().getEnabled()) {
            return;
        }
        ActionTracker actionTracker = ActionTracker.INSTANCE;
        actionTracker.setFishFished(actionTracker.getFishFished() + 1);
    }
}
