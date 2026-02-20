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


import net.chariskar.breakthemod.client.api.engine.PlayerInfo;
import net.chariskar.breakthemod.client.utils.Config;
import net.chariskar.breakthemod.client.utils.ServerUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.hud.category.rule.resolver.ObjectCategoryRuleResolver;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.state.RadarList;
import org.spongepowered.asm.mixin.Shadow;
import xaero.hud.minimap.radar.state.RadarState;
import xaero.hud.minimap.radar.state.RadarStateUpdater;

import java.util.List;
import java.util.Map;

@Pseudo
@Mixin(value = RadarStateUpdater.class, remap = false)
public abstract class EntityRadar {

    @Shadow private Entity lastRenderEntity;
    @Shadow private RadarState state;
    @Shadow private EntityRadarCategoryManager categoryManager;
    @Shadow private Map<EntityRadarCategory, RadarList> updateMap;

    @Shadow
    protected abstract void ensureCategories(
            RadarState state,
            EntityRadarCategory rootCategory,
            List<RadarList> radarLists
    );

    @Shadow
    protected abstract boolean isWorldMapRadarEnabled();


    @Accessor("updateMap")
    abstract Map<EntityRadarCategory, RadarList> getUpdateMap();


    @Inject(
            method = "update",
            at = @At("HEAD"),
            cancellable = true
    )
    private void replaceUpdate(
            ClientWorld world,
            Entity renderEntity,
            PlayerEntity player,
            CallbackInfo ci
    ) {
        boolean enabled = ServerUtils.INSTANCE.getEnabled();
        if (renderEntity == null) {
            renderEntity = this.lastRenderEntity;
        }

        List<RadarList> radarLists = ((RadarStateAccessor) this.state).callGetUpdatableLists();
        EntityRadarCategory rootCategory = this.categoryManager.getRootCategory();
        this.ensureCategories(this.state, rootCategory, radarLists);
        radarLists.forEach(RadarList::clearEntities);
        if (!Config.INSTANCE.getXaerosRdr() && !Config.INSTANCE.getRadar()) { ci.cancel();return; }

        //if (HudMod.INSTANCE.isFairPlay() && !enabled ) { ci.cancel(); return; }
        //if (!HudMod.INSTANCE.getSettings().getEntityRadar() && !this.isWorldMapRadarEnabled()) { ci.cancel(); return; }
        if (world == null || renderEntity == null || player == null) { ci.cancel(); return; }
        //if (Misc.hasEffect(player, Effects.NO_RADAR)) { ci.cancel(); return; }
        //if (Misc.hasEffect(player, Effects.NO_RADAR_HARMFUL)) { ci.cancel(); return; }
        if (!enabled) { ci.cancel(); return; }

        PlayerInfo selfInfo = new PlayerInfo(player.getName().getString(), player.getPos());
        if (selfInfo.shouldSkipSpecial(player)) { ci.cancel(); return; }

        ObjectCategoryRuleResolver resolver = this.categoryManager.getRuleResolver();

        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof PlayerEntity target)) continue;
            if (entity == player) continue;

            PlayerInfo targetInfo = new PlayerInfo(
                    target.getName().getString(),
                    target.getPos()
            );

            if (targetInfo.shouldSkip(target, world)) continue;

            EntityRadarCategory category = resolver.resolve(rootCategory, entity, player);

            if (category == null) continue;
            if (!(Boolean) category.getSettingValue(EntityRadarCategorySettings.DISPLAYED)) continue;

            double offY = renderEntity.getY() - entity.getY();
            int heightLimit = (category.getSettingValue(EntityRadarCategorySettings.HEIGHT_LIMIT)).intValue();

            if (offY * offY > heightLimit * heightLimit) continue;

            RadarList radarList = this.updateMap.get(category);
            int entityLimit = (category.getSettingValue(EntityRadarCategorySettings.ENTITY_NUMBER)).intValue();

            if (entityLimit == 0 || radarList.size() < entityLimit) {
                radarList.add(entity);
            }
        }

        ci.cancel();
    }

}
