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

package net.chariskar.breakthemod.Services;

import net.chariskar.breakthemod.types.Resident;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class friendsService extends Service {
    public List<String> get(String name) {
        List<String> friends = new ArrayList<>();

        CompletableFuture<List<String>> namesFuture =
                CompletableFuture.supplyAsync(() -> {
                    List<String> names = new ArrayList<>();
                    Resident resident = fetch.getResident(name);

                    resident.getFriends()
                            .ifPresent(list ->
                                    list.forEach(ref ->
                                            ref.getName().ifPresent(names::add)
                                    )
                            );
                    return names;
                });

        namesFuture.thenAccept(friends::addAll);

        return friends;
    }
}
