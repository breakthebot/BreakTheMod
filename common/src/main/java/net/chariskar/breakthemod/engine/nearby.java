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

package net.chariskar.breakthemod.engine;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class nearby {
    private static final long UPDATE_INTERVAL_MS = 1000;
    private static final double DISTANCE_THRESHOLD = 200.0;
    private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
    private static final double DIRECTION_STEP = 45.0;

    private final AtomicLong lastUpdateTime = new AtomicLong(0);
    private final List<String> playerInfoList = new ArrayList<>();
    private final StringBuilder stringBuilder = new StringBuilder(128);

    /**
     * High level nearby engine
     * @param self  The player.
     * @param world The world.
     */
    public synchronized Set<String> updateNearbyPlayers(Player self, World world) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime.get() < UPDATE_INTERVAL_MS) {
            return new HashSet<>(playerInfoList);
        }

        lastUpdateTime.set(currentTime);
        playerInfoList.clear();

        Vec3 selfPos = self.getPosition();
        String selfName = self.getName();

        for (Player other : world.getPlayers()) {
            if (other == self || Objects.equals(other.getName(), selfName)) continue;
            if (shouldSkipPlayer(other)) continue;

            Vec3 otherPos = other.getPosition();

            double distanceSquared = selfPos.distanceToSquared(otherPos);
            if (distanceSquared > DISTANCE_THRESHOLD * DISTANCE_THRESHOLD) continue;

            int x = (int) Math.floor(otherPos.x());
            int y = (int) Math.floor(otherPos.y());
            int z = (int) Math.floor(otherPos.z());

            if (!isPlayerUnderBlock(world, x, y, z)) {
                double distance = Math.sqrt(distanceSquared);
                String direction = getDirectionFromYaw(other.getYaw());

                stringBuilder.setLength(0);
                stringBuilder.append("- ")
                        .append(other.getName())
                        .append(" (")
                        .append(x)
                        .append(", ")
                        .append(z)
                        .append(") direction: ")
                        .append(direction)
                        .append(", distance: ")
                        .append(String.format("%.1f", distance))
                        .append(" blocks");

                playerInfoList.add(stringBuilder.toString());
            }
        }

        if (playerInfoList.isEmpty()) {
            playerInfoList.add("No players nearby");
        }

        return new HashSet<>(playerInfoList);
    }

    private boolean shouldSkipPlayer(Player player) {
        return player.isInvisible()
                || player.isSneaking()
                || player.isInVehicle()
                || player.isInNether()
                || player.isInRiptideAnimation();
    }

    private boolean isPlayerUnderBlock(World world, int x, int y, int z) {
        int topY = world.getTopY(x, z);

        if (y >= topY) return false;

        for (int currentY = y + 1; currentY <= topY; currentY++) {
            if (!world.isBlockAirAt(x, currentY, z)) {
                return true;
            }
        }
        return false;
    }

    private String getDirectionFromYaw(float yaw) {
        yaw = ((yaw + 180) % 360 + 360) % 360;

        int directionIndex = (int) Math.round(yaw / DIRECTION_STEP) % 8;
        return DIRECTIONS[directionIndex];
    }

    /**
     * Abstraction for the player type.
     */
    public interface Player {
        String getName();

        Vec3 getPosition();

        float getYaw();

        boolean isInvisible();

        boolean isInRiptideAnimation();

        boolean isInNether();

        boolean isInVehicle();

        boolean isSneaking();
    }

    /**
     * Abstraction for the world type.
     */
    public interface World {
        Iterable<Player> getPlayers();

        boolean isBlockAirAt(int x, int y, int z);

        int getTopY(int x, int z);
    }

    /**
     * Abstraction for the 3d vec type
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    public record Vec3(double x, double y, double z) {
        public double distanceToSquared(Vec3 other) {
            double dx = x - other.x;
            double dy = y - other.y;
            double dz = z - other.z;
            return dx * dx + dy * dy + dz * dz;
        }

        public double distanceTo(Vec3 other) {
            return Math.sqrt(distanceToSquared(other));
        }
    }
}