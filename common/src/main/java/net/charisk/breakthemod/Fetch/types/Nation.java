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

package net.charisk.breakthemod.Fetch.types;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Nation {
    public String name;                       // required
    public UUID uuid;
    public String board;
    public String dynmapColour;
    public String dynmapOutline;
    public String wiki;
    public Leader king;
    public Capital capital;
    public Timestamps timestamps;
    public Status status;
    public Stats stats;
    public Coordinates coordinates;
    public List<Resident> residents;
    public List<Resident> towns;
    public List<Resident> allies;
    public List<Resident> enemies;
    public List<Resident> sanctioned;
    public Ranks ranks;

    public Nation(String name) {
        this.name = name;
    }

    // Getters that return Optional for null safety
    public String getName() { return name; }
    public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    public Optional<String> getBoard() { return Optional.ofNullable(board); }
    public Optional<String> getDynmapColour() { return Optional.ofNullable(dynmapColour); }
    public Optional<String> getDynmapOutline() { return Optional.ofNullable(dynmapOutline); }
    public Optional<String> getWiki() { return Optional.ofNullable(wiki); }
    public void setWiki(String wiki) { this.wiki = wiki; }
    public Optional<Leader> getKing() { return Optional.ofNullable(king); }
    public Optional<Capital> getCapital() { return Optional.ofNullable(capital); }
    public Optional<Timestamps> getTimestamps() { return Optional.ofNullable(timestamps); }
    public Optional<Status> getStatus() { return Optional.ofNullable(status); }
    public Optional<Stats> getStats() { return Optional.ofNullable(stats); }
    public Optional<Coordinates> getCoordinates() { return Optional.ofNullable(coordinates); }
    public Optional<List<Resident>> getResidents() { return Optional.ofNullable(residents); }
    public Optional<List<Resident>> getTowns() { return Optional.ofNullable(towns); }
    public Optional<List<Resident>> getAllies() { return Optional.ofNullable(allies); }
    public Optional<List<Resident>> getEnemies() { return Optional.ofNullable(enemies); }
    public Optional<List<Resident>> getSanctioned() { return Optional.ofNullable(sanctioned); }
    public Optional<Ranks> getRanks() { return Optional.ofNullable(ranks); }

    public static class Leader {
        public String name;
        public UUID uuid;
        public Optional<String> getName() { return Optional.ofNullable(name); }
        public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    }

    public static class Capital {
        public String name;
        public UUID uuid;
        public Optional<String> getName() { return Optional.ofNullable(name); }
        public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    }

    public static class Timestamps {
        public Long registered;
        public Optional<Long> getRegistered() { return Optional.ofNullable(registered); }
    }

    public static class Status {
        public Boolean isPublic;
        public Boolean isOpen;
        public Boolean isNeutral;
        public Optional<Boolean> getIsPublic() { return Optional.ofNullable(isPublic); }
        public Optional<Boolean> getIsOpen() { return Optional.ofNullable(isOpen); }
        public Optional<Boolean> getIsNeutral() { return Optional.ofNullable(isNeutral); }
    }

    public static class Stats {
        public Integer numTownBlocks;
        public Integer numResidents;
        public Integer numTowns;
        public Integer numAllies;
        public Integer numEnemies;
        public Integer balance;
        public Optional<Integer> getNumTownBlocks() { return Optional.ofNullable(numTownBlocks); }
        public Optional<Integer> getNumResidents() { return Optional.ofNullable(numResidents); }
        public Optional<Integer> getNumTowns() { return Optional.ofNullable(numTowns); }
        public Optional<Integer> getNumAllies() { return Optional.ofNullable(numAllies); }
        public Optional<Integer> getNumEnemies() { return Optional.ofNullable(numEnemies); }
        public Optional<Integer> getBalance() { return Optional.ofNullable(balance); }
    }

    public static class Coordinates {
        public Spawn spawn;
        public Optional<Spawn> getSpawn() { return Optional.ofNullable(spawn); }
    }

    public static class Spawn {
        public String world;
        public Double x;
        public Double y;
        public Double z;
        public Float pitch;
        public Float yaw;
        public Optional<String> getWorld() { return Optional.ofNullable(world); }
        public Optional<Double> getX() { return Optional.ofNullable(x); }
        public Optional<Double> getY() { return Optional.ofNullable(y); }
        public Optional<Double> getZ() { return Optional.ofNullable(z); }
        public Optional<Float> getPitch() { return Optional.ofNullable(pitch); }
        public Optional<Float> getYaw() { return Optional.ofNullable(yaw); }
    }

    public static class Resident {
        public String name;
        public UUID uuid;
        public Optional<String> getName() { return Optional.ofNullable(name); }
        public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    }

    public static class Ranks {
        public List<Resident> Chancellor;
        public List<Resident> Colonist;
        public List<Resident> Diplomat;
        public Optional<List<Resident>> getChancellor() { return Optional.ofNullable(Chancellor); }
        public Optional<List<Resident>> getColonist() { return Optional.ofNullable(Colonist); }
        public Optional<List<Resident>> getDiplomat() { return Optional.ofNullable(Diplomat); }
    }
}