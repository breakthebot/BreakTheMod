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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;
import java.util.List;

/**
 * World class wrapper for the nearby engine {@link net.chariskar.breakthemod.engine.nearby.World}
 */
public class world implements nearby.World {
    private final ClientWorld world;

    public world(ClientWorld world) {
        this.world = world;
    }

    @Override
    public Iterable<nearby.Player> getPlayers() {
        List<nearby.Player> players = new ArrayList<>();
        for (PlayerEntity player : world.getPlayers()) {
            players.add(new player(player));
        }
        return players;
    }

    @Override
    public boolean isBlockAirAt(int x, int y, int z) {
        return world.getBlockState(new BlockPos(x, y, z)).isAir();
    }

    @Override
    public int getTopY(int x, int z) {
        return world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
    }
}