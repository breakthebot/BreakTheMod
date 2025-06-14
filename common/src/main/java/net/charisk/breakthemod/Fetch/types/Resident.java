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

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class Resident {
    private final String name;                       // required
    private UUID uuid;
    private String title;
    private String surname;
    private String formattedName;
    private String about;
    private Reference town;
    private Reference nation;
    private Timestamps timestamps;
    private Status status;
    private Stats stats;
    private Perms perms;
    private Ranks ranks;
    private List<Reference> friends;

    public Resident(String name) {
        this.name = name;
    }

    // getters & setters
    public String getName() { return name; }

    public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }

    public Optional<String> getTitle() { return Optional.ofNullable(title); }

    public Optional<String> getSurname() { return Optional.ofNullable(surname); }

    public Optional<String> getFormattedName() { return Optional.ofNullable(formattedName); }

    public Optional<String> getAbout() { return Optional.ofNullable(about); }

    public Optional<Reference> getTown() { return Optional.ofNullable(town); }

    public Optional<Reference> getNation() { return Optional.ofNullable(nation); }

    public Optional<Timestamps> getTimestamps() { return Optional.ofNullable(timestamps); }

    public Optional<Status> getStatus() { return Optional.ofNullable(status); }

    public Optional<Stats> getStats() { return Optional.ofNullable(stats); }

    public Optional<Perms> getPerms() { return Optional.ofNullable(perms); }

    public Optional<Ranks> getRanks() { return Optional.ofNullable(ranks); }

    public Optional<List<Reference>> getFriends() { return Optional.ofNullable(friends); }

    // Nested static classes

    public static class Reference {
        public String name;
        public UUID uuid;

        public Optional<String> getName() { return Optional.ofNullable(name); }

        public Optional<UUID> getUuid() { return Optional.ofNullable(uuid); }
    }

    public static class Timestamps {
        public Long registered;
        public Long joinedTownAt;
        public Long lastOnline;

        public Optional<Long> getRegistered() { return Optional.ofNullable(registered); }

        public Optional<Long> getJoinedTownAt() { return Optional.ofNullable(joinedTownAt); }

        public Optional<Long> getLastOnline() { return Optional.ofNullable(lastOnline); }
    }

    public static class Status {
        private Boolean isOnline;
        private Boolean isNPC;
        private Boolean isMayor;
        private Boolean isKing;
        private Boolean hasTown;
        private Boolean hasNation;

        public Optional<Boolean> getIsOnline() { return Optional.ofNullable(isOnline); }

        public Optional<Boolean> getIsNPC() { return Optional.ofNullable(isNPC); }

        public Optional<Boolean> getIsMayor() { return Optional.ofNullable(isMayor); }

        public Optional<Boolean> getIsKing() { return Optional.ofNullable(isKing); }

        public Optional<Boolean> getHasTown() { return Optional.ofNullable(hasTown); }

        public Optional<Boolean> getHasNation() { return Optional.ofNullable(hasNation); }
    }

    public static class Stats {
        public Integer balance;
        public Integer numFriends;

        public Optional<Integer> getBalance() { return Optional.ofNullable(balance); }

        public Optional<Integer> getNumFriends() { return Optional.ofNullable(numFriends); }
    }

    public static class Perms {
        public List<Boolean> build;
        public List<Boolean> destroy;
        @SerializedName("switch")
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

    public static class Ranks {
        public List<String> townRanks;
        public List<String> nationRanks;

        public Optional<List<String>> getTownRanks() { return Optional.ofNullable(townRanks); }

        public Optional<List<String>> getNationRanks() { return Optional.ofNullable(nationRanks); }
    }
}