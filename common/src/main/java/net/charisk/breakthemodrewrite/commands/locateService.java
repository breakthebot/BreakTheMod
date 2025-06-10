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

package net.charisk.breakthemodrewrite.commands;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.charisk.breakthemodrewrite.Fetch.fetch;

public class locateService {
    private fetch fetchInstance;

    public static class LocationResult {
        private final String name;
        private final int x;
        private final int z;
        private final String mapUrl;

        public LocationResult(String name, int x, int z) {
            this.name = name;
            this.x = x;
            this.z = z;
            this.mapUrl = String.format(
                    "https://map.earthmc.net/?world=minecraft_overworld&zoom=3&x=%d&z=%d",
                    x, z
            );
        }

        public String getName() { return name; }
        public int getX() { return x; }
        public int getZ() { return z; }
        public String getMapUrl() { return mapUrl; }
    }

    public enum LocationType {
        TOWN("https://api.earthmc.net/v3/aurora/towns"),
        NATION("https://api.earthmc.net/v3/aurora/nations");

        private final String apiUrl;

        LocationType(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getApiUrl() { return apiUrl; }

        public static LocationType fromString(String type) {
            return switch (type.toLowerCase()) {
                case "town" -> TOWN;
                case "nation" -> NATION;
                default -> throw new IllegalArgumentException("Invalid type: " + type + ". Use 'town' or 'nation'.");
            };
        }
    }

    public locateService() {
        this.fetchInstance = new fetch();
    }

    public LocationResult getLocation(String name, LocationType type) {
        try {
            JsonObject payload = buildPayload(name);
            String response = fetchInstance.PostRequest(type.getApiUrl(), payload.toString());
            JsonArray responseArray = JsonParser.parseString(response).getAsJsonArray();

            if (responseArray.size() == 0) {
                return null;
            }

            JsonObject coordinates = responseArray.get(0).getAsJsonObject()
                    .get("coordinates").getAsJsonObject()
                    .get("spawn").getAsJsonObject();

            int x = coordinates.get("x").getAsInt();
            int z = coordinates.get("z").getAsInt();

            return new LocationResult(name, x, z);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonObject buildPayload(String name) {
        JsonObject payload = new JsonObject();
        JsonArray queryArray = new JsonArray();
        queryArray.add(name);
        payload.add("query", queryArray);

        JsonObject template = new JsonObject();
        template.addProperty("coordinates", true);
        payload.add("template", template);

        return payload;
    }
}