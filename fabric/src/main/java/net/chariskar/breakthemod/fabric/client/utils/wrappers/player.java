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

package net.chariskar.breakthemod.fabric.client.utils.wrappers;

import net.chariskar.breakthemod.engine.nearby;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Player class wrapper for the nearby engine {@link net.chariskar.breakthemod.engine.nearby.Player}
 */
public class player implements nearby.Player {
    private final PlayerEntity player;

    public player(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName().getString();
    }

    @Override
    public nearby.Vec3 getPosition() {
        return new nearby.Vec3(player.getX(), player.getY(), player.getZ());
    }

    @Override
    public float getYaw() {
        return player.getYaw();
    }

    @Override
    public boolean isInvisible() {
        return player.isInvisible();
    }

    @Override
    public boolean isInRiptideAnimation() {
        return player.isUsingRiptide();
    }

    @Override
    public boolean isInNether() {
        return player.getWorld().getRegistryKey().getValue().toString().contains("nether");
    }

    @Override
    public boolean isInVehicle() {
        return player.hasVehicle();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }
}
