/*
 * This file is part of breakthemodrewrite.
 *
 * breakthemodrewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodrewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodrewrite. If not, see <https://www.gnu.org/licenses/>.
 */

package net.charisk.breakthemodrewrite.engine;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class nearby {
    private static final long UPDATE_INTERVAL_MS = 1000;
    private final AtomicLong lastUpdateTime = new AtomicLong(0);

    private final Set<String> playerInfoList = new HashSet<>();

    public record Vec3(double x, double y, double z) {
        public double distanceTo(Vec3 other) {
            double dx = x - other.x;
            double dy = y - other.y;
            double dz = z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

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
    public interface World {
        Iterable<Player> getPlayers();
        boolean isBlockAirAt(int x, int y, int z);
        int getTopY(int x, int z);
    }


    public synchronized Set<String> updateNearbyPlayers(Player self, World world) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime.get() < UPDATE_INTERVAL_MS) {
            return Set.copyOf(playerInfoList);
        }

        lastUpdateTime.set(currentTime);
        playerInfoList.clear();

        for (Player other : world.getPlayers()) {
            if (Objects.equals(other.getName(), self.getName())) continue;
            if (other == self) continue;
            if (shouldSkipPlayer(other)) continue;

            Vec3 pos = other.getPosition();
            int x = (int) Math.floor(pos.x());
            int y = (int) Math.floor(pos.y());
            int z = (int) Math.floor(pos.z());

            if (!isPlayerUnderBlock(world, x, y, z)) {
                double distance = self.getPosition().distanceTo(pos);
                String direction = getDirectionFromYaw(other.getYaw());

                playerInfoList.add(String.format(
                        "- %s (%d, %d) direction: %s, distance: %.1f blocks",
                        other.getName(), x, z, direction, distance
                ));
            }
        }

        if (playerInfoList.isEmpty()) {
            playerInfoList.add("No players nearby");
        }

        return Set.copyOf(playerInfoList);
    }

    private boolean shouldSkipPlayer(Player player) {
        return player.isInvisible() || player.isInRiptideAnimation() || player.isInNether()
                || player.isInVehicle() || player.isSneaking();
    }

    private boolean isPlayerUnderBlock(World world, int x, int y, int z) {
        int topY = world.getTopY(x, z);
        for (int currentY = y + 1; currentY <= topY; currentY++) {
            if (!world.isBlockAirAt(x, currentY, z)) {
                return true;
            }
        }
        return false;
    }

    private String getDirectionFromYaw(float yaw) {
        yaw = (yaw + 180) % 360;
        if (yaw < 0) yaw += 360;

        if (yaw >= 337.5 || yaw < 22.5) return "S";
        if (yaw >= 22.5 && yaw < 67.5) return "SW";
        if (yaw >= 67.5 && yaw < 112.5) return "W";
        if (yaw >= 112.5 && yaw < 157.5) return "NW";
        if (yaw >= 157.5 && yaw < 202.5) return "N";
        if (yaw >= 202.5 && yaw < 247.5) return "NE";
        if (yaw >= 247.5 && yaw < 292.5) return "E";
        if (yaw >= 292.5 && yaw < 337.5) return "SE";
        return "Unknown";
    }
}
