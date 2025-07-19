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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.chariskar.breakthemod.api.Fetch;
import net.chariskar.breakthemod.utils.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class locateService {
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private Fetch fetchInstance;

    public locateService() {
        this.fetchInstance = Fetch.getInstance();
    }

    public LocationResult getLocation(String name, LocationType type) {
        try {
            JsonObject payload = buildPayload(name);
            String response = fetchInstance.PostRequest(type.getApiUrl(), payload.toString()).body();
            JsonArray responseArray = JsonParser.parseString(response).getAsJsonArray();

            if (responseArray.isEmpty()) {
                return null;
            }

            JsonObject coordinates = responseArray.get(0).getAsJsonObject()
                    .get("coordinates").getAsJsonObject()
                    .get("spawn").getAsJsonObject();

            int x = coordinates.get("x").getAsInt();
            int z = coordinates.get("z").getAsInt();

            return new LocationResult(name, x, z);

        } catch (Exception e) {
            if (config.getInstance().isDev()) {
                e.printStackTrace();
            } else {
                LOGGER.error("Unexpected Error Occurred");
            }
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

    public enum LocationType {
        TOWN(config.getInstance().getApiURL() + "/towns"),
        NATION(config.getInstance().getApiURL() + "/nations");

        private final String apiUrl;

        LocationType(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public static LocationType fromString(String type) {
            return switch (type.toLowerCase()) {
                case "town" -> TOWN;
                case "nation" -> NATION;
                default -> throw new IllegalArgumentException("Invalid type: " + type + ". Use 'town' or 'nation'.");
            };
        }

        public String getApiUrl() {
            return apiUrl;
        }
    }

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
                    "%s?world=minecraft_overworld&zoom=3&x=%d&z=%d",
                    config.getInstance().getMapURL(), x, z
            );
        }

        public String getName() {
            return name;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        public String getMapUrl() {
            return mapUrl;
        }
    }
}