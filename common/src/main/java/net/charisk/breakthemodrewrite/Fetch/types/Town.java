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

package net.charisk.breakthemodrewrite.Fetch.types;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Town {
    public String name;
    public UUID uuid;
    public String board;
    public String founder;
    public String wiki;
    public Mayor mayor;
    public Nation nation;
    public Timestamps timestamps;
    public Status status;
    public Stats stats;
    public Perms perms;
    public Coordinates coordinates;
    public List<Resident> residents;
    public List<Resident> trusted;
    public List<Resident> outlaws;
    public List<UUID> quarters;
    public Ranks ranks;

    public Town(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    public Optional<String> getBoard() { return Optional.ofNullable(board); }
    public Optional<String> getFounder() { return Optional.ofNullable(founder); }
    public Optional<String> getWiki() { return Optional.ofNullable(wiki); }
    public Optional<Mayor> getMayor() { return Optional.ofNullable(mayor); }
    public Optional<Nation> getNation() { return Optional.ofNullable(nation); }
    public Optional<Timestamps> getTimestamps() { return Optional.ofNullable(timestamps); }
    public Optional<Status> getStatus() { return Optional.ofNullable(status); }
    public Optional<Stats> getStats() { return Optional.ofNullable(stats); }
    public Optional<Perms> getPerms() { return Optional.ofNullable(perms); }
    public Optional<Coordinates> getCoordinates() { return Optional.ofNullable(coordinates); }
    public Optional<List<Resident>> getResidents() { return Optional.ofNullable(residents); }
    public Optional<List<Resident>> getTrusted() { return Optional.ofNullable(trusted); }
    public Optional<List<Resident>> getOutlaws() { return Optional.ofNullable(outlaws); }
    public Optional<List<UUID>> getQuarters() { return Optional.ofNullable(quarters); }
    public Optional<Ranks> getRanks() { return Optional.ofNullable(ranks); }

    public static class Mayor {
        public String name;
        public UUID uuid;
        public Optional<String> getName() { return Optional.ofNullable(name); }
        public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    }

    public static class Nation {
        public String name;
        public UUID uuid;
        public Optional<String> getName() { return Optional.ofNullable(name); }
        public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    }

    public static class Timestamps {
        public Long registered;
        public Long joinedNationAt;
        public Long ruinedAt;
        public Optional<Long> getRegistered() { return Optional.ofNullable(registered); }
        public Optional<Long> getJoinedNationAt() { return Optional.ofNullable(joinedNationAt); }
        public Optional<Long> getRuinedAt() { return Optional.ofNullable(ruinedAt); }
    }

    public static class Status {
        public Boolean isPublic;
        public Boolean isOpen;
        public Boolean isNeutral;
        public Boolean isCapital;
        public Boolean isOverClaimed;
        public Boolean isRuined;
        public Boolean isForSale;
        public Boolean hasNation;
        public Boolean hasOverclaimShield;
        public Boolean canOutsidersSpawn;
        public Optional<Boolean> getIsPublic() { return Optional.ofNullable(isPublic); }
        public Optional<Boolean> getIsOpen() { return Optional.ofNullable(isOpen); }
        public Optional<Boolean> getIsNeutral() { return Optional.ofNullable(isNeutral); }
        public Optional<Boolean> getIsCapital() { return Optional.ofNullable(isCapital); }
        public Optional<Boolean> getIsOverClaimed() { return Optional.ofNullable(isOverClaimed); }
        public Optional<Boolean> getIsRuined() { return Optional.ofNullable(isRuined); }
        public Optional<Boolean> getIsForSale() { return Optional.ofNullable(isForSale); }
        public Optional<Boolean> getHasNation() { return Optional.ofNullable(hasNation); }
        public Optional<Boolean> getHasOverclaimShield() { return Optional.ofNullable(hasOverclaimShield); }
        public Optional<Boolean> getCanOutsidersSpawn() { return Optional.ofNullable(canOutsidersSpawn); }
    }

    public static class Stats {
        public Integer numTownBlocks;
        public Integer maxTownBlocks;
        public Integer numResidents;
        public Integer numTrusted;
        public Integer numOutlaws;
        public Integer balance;
        public Integer forSalePrice;
        public Optional<Integer> getNumTownBlocks() { return Optional.ofNullable(numTownBlocks); }
        public Optional<Integer> getMaxTownBlocks() { return Optional.ofNullable(maxTownBlocks); }
        public Optional<Integer> getNumResidents() { return Optional.ofNullable(numResidents); }
        public Optional<Integer> getNumTrusted() { return Optional.ofNullable(numTrusted); }
        public Optional<Integer> getNumOutlaws() { return Optional.ofNullable(numOutlaws); }
        public Optional<Integer> getBalance() { return Optional.ofNullable(balance); }
        public Optional<Integer> getForSalePrice() { return Optional.ofNullable(forSalePrice); }
    }

    public static class Perms {
        public List<Boolean> build;
        public List<Boolean> destroy;
        public List<Boolean> switchPerm;
        public List<Boolean> itemUse;
        public Flags flags;
        public Optional<List<Boolean>> getBuild() { return Optional.ofNullable(build); }
        public Optional<List<Boolean>> getDestroy() { return Optional.ofNullable(destroy); }
        public Optional<List<Boolean>> getSwitchPerm() { return Optional.ofNullable(switchPerm); }
        public Optional<List<Boolean>> getItemUse() { return Optional.ofNullable(itemUse); }
        public Optional<Flags> getFlags() { return Optional.ofNullable(flags); }
    }

    public static class Flags {
        public Boolean pvp;
        public Boolean explosion;
        public Boolean fire;
        public Boolean mobs;
        public Optional<Boolean> getPvp() { return Optional.ofNullable(pvp); }
        public Optional<Boolean> getExplosion() { return Optional.ofNullable(explosion); }
        public Optional<Boolean> getFire() { return Optional.ofNullable(fire); }
        public Optional<Boolean> getMobs() { return Optional.ofNullable(mobs); }
    }

    public static class Coordinates {
        public Spawn spawn;
        public List<Integer> homeBlock;
        public List<List<Integer>> townBlocks;
        public Optional<Spawn> getSpawn() { return Optional.ofNullable(spawn); }
        public Optional<List<Integer>> getHomeBlock() { return Optional.ofNullable(homeBlock); }
        public Optional<List<List<Integer>>> getTownBlocks() { return Optional.ofNullable(townBlocks); }
    }

    public static class Spawn {
        public String world;
        public Double x;
        public Integer y;
        public Double z;
        public Float pitch;
        public Float yaw;
        public Optional<String> getWorld() { return Optional.ofNullable(world); }
        public Optional<Double> getX() { return Optional.ofNullable(x); }
        public Optional<Integer> getY() { return Optional.ofNullable(y); }
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
        public List<Resident> Councillor;
        public List<Resident> Builder;
        public List<Resident> Recruiter;
        public List<Resident> Police;
        public List<Resident> TaxExempt;
        public List<Resident> Treasurer;
        public List<Resident> Realtor;
        public List<Resident> Settler;
        public Optional<List<Resident>> getCouncillor() { return Optional.ofNullable(Councillor); }
        public Optional<List<Resident>> getBuilder() { return Optional.ofNullable(Builder); }
        public Optional<List<Resident>> getRecruiter() { return Optional.ofNullable(Recruiter); }
        public Optional<List<Resident>> getPolice() { return Optional.ofNullable(Police); }
        public Optional<List<Resident>> getTaxExempt() { return Optional.ofNullable(TaxExempt); }
        public Optional<List<Resident>> getTreasurer() { return Optional.ofNullable(Treasurer); }
        public Optional<List<Resident>> getRealtor() { return Optional.ofNullable(Realtor); }
        public Optional<List<Resident>> getSettler() { return Optional.ofNullable(Settler); }
    }
}