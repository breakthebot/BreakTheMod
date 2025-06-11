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

package net.charisk.breakthemodrewrite.Services;
import net.charisk.breakthemodrewrite.api.Fetch;
import net.charisk.breakthemodrewrite.Fetch.types.Resident;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class friendsService {
    private Fetch fetch = new Fetch();
    private static final ExecutorService IO_EXECUTOR = Executors.newFixedThreadPool(4);

    public List<String> get(String name){
        Resident resident = fetch.getResident(name);
        List<String> friends = new ArrayList<>();


        CompletableFuture<List<String>> namesFuture =
                CompletableFuture.supplyAsync(() -> {
                    List<String> names = new ArrayList<>();
                    resident.getFriends()
                            .ifPresent(list ->
                                    list.forEach(ref ->
                                            ref.getName().ifPresent(names::add)
                                    )
                            );
                    return names;
                }, IO_EXECUTOR);

        namesFuture.thenAccept(friends::addAll);

        return friends;
    }
}
