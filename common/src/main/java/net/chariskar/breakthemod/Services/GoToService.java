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


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.chariskar.breakthemod.types.Nation;
import net.chariskar.breakthemod.types.Town;
import net.chariskar.breakthemod.utils.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoToService extends Service {
    private static final ExecutorService IO_EXECUTOR = Executors.newFixedThreadPool(4);

    /**
     * Finds the nearest valid town spawns for the given name.
     * @param townName The town to search spawns for
     * @return future completing with either a list of town names or throws on fatal error.
     */
    public CompletableFuture<List<String>> findValidTowns(String townName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> valid = new ArrayList<>();

                Town reqTown = fetch.getTown(townName);

                if (reqTown.getStatus().get().isCapital){
                    String nationUuid = reqTown.getNation().get().getName().get();
                    Nation nation = fetch.getNation(nationUuid);
                    if (nation.getStatus().get().isPublic) {
                        valid.add(reqTown.getName());
                    }
                    return valid;
                } else if (reqTown.getStatus().get().isPublic && reqTown.getStatus().get().canOutsidersSpawn) {
                    valid.add(reqTown.getName());
                    return valid;
                }


                int radius = 500;
                int attempts = 3;

                while (attempts-- > 0) {
                    JsonObject payload = new JsonObject();
                    JsonArray queryArr = new JsonArray();
                    JsonObject query = new JsonObject();
                    query.addProperty("target_type", "TOWN");
                    query.addProperty("target", townName);
                    query.addProperty("search_type", "TOWN");
                    query.addProperty("radius", radius);
                    queryArr.add(query);
                    payload.add("query", queryArr);

                    String nearbyUrl = config.getInstance().getApiURL() + "/nearby";
                    String nearbyResp = fetch.PostRequest(nearbyUrl, payload.toString()).body();
                    JsonArray nearbyArray = JsonParser.parseString(nearbyResp)
                            .getAsJsonArray().get(0).getAsJsonArray();


                    List<String> names = new ArrayList<>();
                    for (JsonElement el : nearbyArray) {
                        JsonObject obj = el.getAsJsonObject();
                        if (obj.has("name")) {
                            names.add(obj.get("name").getAsString());
                        }
                    }

                    if (names.isEmpty()) {
                        radius += 500;
                        continue;
                    }

                    List<Town> towns = fetch.getTowns(names);

                    for (Town town : towns) {
                        Town.Status status = town.getStatus().get();
                        if (status.isPublic && status.canOutsidersSpawn) {
                            valid.add(town.getName());
                        } else if (status.isCapital) {
                            String nationUuid = town.getNation().get().getName().get();
                            Nation nation = fetch.getNation(nationUuid);
                            if (nation.getStatus().get().isPublic) {
                                valid.add(town.getName());
                            }
                        }
                    }

                    if (!valid.isEmpty()) {
                        return valid;
                    }

                    radius += 500;
                }

                return Collections.emptyList();

            } catch (Exception e) {
                logError("Unexpected error occurred while fetching location", e);
                throw new RuntimeException(e);
            }
        }, IO_EXECUTOR);
    }
}