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

package net.chariskar.breakthemod.types;


import java.util.Optional;
import java.util.UUID;

public class reference {
    public String name;
    public UUID uuid;

    public reference(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<UUID> getUuid() {
        return Optional.ofNullable(uuid);
    }
}
