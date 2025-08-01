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
import net.chariskar.breakthemod.utils.config;

import java.net.http.HttpResponse;
import java.util.Optional;

public class findPlayerService extends Service {

    public Optional<PlayerLocationInfo> get(String username) {
        try {
            HttpResponse<String> resp = fetch.GetRequest("https://map.earthmc.net/tiles/players.json");
            JsonArray playersJson = JsonParser.parseString(resp.body()).getAsJsonObject().get("players").getAsJsonArray();


            for (JsonElement playerElement : playersJson) {
                JsonObject user = playerElement.getAsJsonObject();
                String name = user.get("name").getAsString();

                if (name.equalsIgnoreCase(username)) {
                    double x = user.get("x").getAsDouble();
                    double z = user.get("z").getAsDouble();

                    JsonObject payload = new JsonObject();
                    JsonArray queryArray = new JsonArray();
                    JsonArray coords = new JsonArray();
                    coords.add(x);
                    coords.add(z);
                    queryArray.add(coords);
                    payload.add("query", queryArray);

                    String locationJson = fetch.PostRequest(config.getInstance().getApiURL() + "/location", payload.toString()).body();
                    JsonArray locationData = JsonParser.parseString(locationJson).getAsJsonArray();

                    if (!locationData.isEmpty() && locationData.get(0).isJsonObject()) {
                        JsonObject data = locationData.get(0).getAsJsonObject();
                        boolean isWilderness = data.get("isWilderness").getAsBoolean();
                        String townName = "Unknown";

                        if (!isWilderness && data.has("town") && data.get("town").isJsonObject()) {
                            JsonObject town = data.getAsJsonObject("town");
                            townName = town.has("name") ? town.get("name").getAsString() : "Unknown";
                        }

                        return Optional.of(new PlayerLocationInfo(username, x, z, isWilderness, townName, true));
                    }
                }
            }

            return Optional.of(new PlayerLocationInfo(username, 0, 0, true, "N/A", false));

        } catch (Exception e) {
            logError("Unexpected error fetching player location", e);
            return Optional.empty();
        }
    }

    public static class PlayerLocationInfo {
        public final String username;
        public final double x;
        public final double z;
        public final boolean isWilderness;
        public final String townName;
        public boolean found = false;

        public PlayerLocationInfo(String username, double x, double z, boolean isWilderness, String townName, boolean found) {
            this.username = username;
            this.x = x;
            this.z = z;
            this.isWilderness = isWilderness;
            this.townName = townName;
            this.found = found;
        }

        @Override
        public String toString() {
            if (!found) {
                return username + " is either offline or not showing up on the map.";
            } else if (isWilderness) {
                return username + " at x: " + x + ", z: " + z + " is in wilderness.";
            } else {
                return username + " at x: " + x + ", z: " + z + " is in town: " + townName + ".";
            }
        }
    }
}
