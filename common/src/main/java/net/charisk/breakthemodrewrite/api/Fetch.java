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
 
package net.charisk.breakthemodrewrite.api;
import com.google.gson.*;
import net.charisk.breakthemodrewrite.Fetch.fetch;
import net.charisk.breakthemodrewrite.Fetch.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @note This is the high level fetch class, if you want the fetch types, which should not be used directly in the mod, you need to go to Fetch.types
*/
public class Fetch {
    private final fetch Fetch;
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private final Gson gson = new Gson();
    public Fetch() {this.Fetch = new fetch();}
    
    public Town getTown(String name) {
        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        query.add(name);
        payload.add("query", query);
        try {
            String response = this.Fetch.PostRequest("https://api.earthmc.net/v3/aurora/towns", payload.toString());
            JsonObject townJson = JsonParser
                    .parseString(response)
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject();
            return gson.fromJson(townJson, Town.class);
        } catch (Exception e) {
            LOGGER.error("Unexpected error fetching town '{}': {}", name, e.getMessage(), e);
        }
        return null;
    }

    public List<Town> getTowns(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        names.forEach(query::add);
        payload.add("query", query);

        try {
            String response = this.Fetch.PostRequest("https://api.earthmc.net/v3/aurora/towns", payload.toString());
            JsonArray townsJson = JsonParser.parseString(response).getAsJsonArray();

            List<Town> towns = new ArrayList<>();
            for (JsonElement element : townsJson) {
                try {
                    Town town = gson.fromJson(element.getAsJsonObject(), Town.class);
                    towns.add(town);
                } catch (Exception e) {
                    LOGGER.warn("Failed to parse town from response: {}", e.getMessage());
                }
            }
            return towns;
        } catch (Exception e) {
            LOGGER.error("Unexpected error fetching towns {}: {}", names, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public List<Town> getTowns(String... names) {
        return getTowns(Arrays.asList(names));
    }

    public Nation getNation(String name) {
        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        query.add(name);
        payload.add("query", query);
        try {
            String response = this.Fetch.PostRequest(
                    "https://api.earthmc.net/v3/aurora/nations",
                    payload.toString()
            );

            JsonArray responseArray = JsonParser.parseString(response).getAsJsonArray();
            if (responseArray.size() == 0) {
                LOGGER.warn("Nation '{}' not found", name);
                return null;
            }

            JsonObject nationJson = responseArray.get(0).getAsJsonObject();
            return gson.fromJson(nationJson, Nation.class);

        } catch (JsonSyntaxException e) {
            LOGGER.error("Invalid JSON response for nation '{}': {}", name, e.getMessage());
            LOGGER.debug("Raw response might be malformed JSON", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error fetching nation '{}': {}", name, e.getMessage(), e);
        }
        return null;
    }

    public List<Nation> getNations(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        names.forEach(query::add);
        payload.add("query", query);

        try {
            String response = this.Fetch.PostRequest(
                    "https://api.earthmc.net/v3/aurora/nations",
                    payload.toString()
            );
            JsonArray nationsJson = JsonParser.parseString(response).getAsJsonArray();

            List<Nation> nations = new ArrayList<>();
            for (JsonElement element : nationsJson) {
                try {
                    Nation nation = gson.fromJson(element.getAsJsonObject(), Nation.class);
                    nations.add(nation);
                } catch (Exception e) {
                    LOGGER.warn("Failed to parse nation from response: {}", e.getMessage());
                }
            }
            return nations;
        } catch (Exception e) {
            LOGGER.error("Unexpected error fetching nations {}: {}", names, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public List<Nation> getNations(String... names) {
        return getNations(Arrays.asList(names));
    }

    public Resident getResident(String name) {
        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        query.add(name);

        payload.add("query", query);
        try {
            String response = this.Fetch.PostRequest(
                    "https://api.earthmc.net/v3/aurora/residents",
                    payload.toString()
            );
            JsonObject playerJson = JsonParser
                    .parseString(response)
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject();
            return gson.fromJson(playerJson, Resident.class);
        } catch (Exception e) {
            LOGGER.error("Unexpected error fetching resident '{}': {}", name, e.getMessage(), e);
        }
        return null;
    }

    public List<Resident> getResidents(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        names.forEach(query::add);
        payload.add("query", query);

        try {
            String response = this.Fetch.PostRequest(
                    "https://api.earthmc.net/v3/aurora/residents",
                    payload.toString()
            );
            JsonArray residentsJson = JsonParser.parseString(response).getAsJsonArray();

            List<Resident> residents = new ArrayList<>();
            for (JsonElement element : residentsJson) {
                try {
                    Resident resident = gson.fromJson(element.getAsJsonObject(), Resident.class);
                    residents.add(resident);
                } catch (Exception e) {
                    LOGGER.warn("Failed to parse resident from response: {}", e.getMessage());
                }
            }
            return residents;
        } catch (Exception e) {
            LOGGER.error("Unexpected error fetching residents {}: {}", names, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

}
