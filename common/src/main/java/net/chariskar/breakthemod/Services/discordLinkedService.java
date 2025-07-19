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

public class discordLinkedService {

    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private final Fetch fetch = Fetch.getInstance();

    public String get(String username) {
        try {
            String mojangResponse = fetch.GetRequest(
                    "https://api.mojang.com/users/profiles/minecraft/" + username
            ).body();

            JsonObject mojangData = JsonParser.parseString(mojangResponse).getAsJsonObject();
            String rawUUID = mojangData.get("id").getAsString();
            String formattedUUID = formatUUID(rawUUID);

            if (!isValidUUID(formattedUUID)) {
                return null;
            }

            JsonObject payload = new JsonObject();
            JsonArray queryArray = new JsonArray();
            JsonObject queryItem = new JsonObject();
            queryItem.addProperty("type", "minecraft");
            queryItem.addProperty("target", formattedUUID);
            queryArray.add(queryItem);
            payload.add("query", queryArray);

            String emcResponse = fetch.PostRequest(
                    config.getInstance().getApiURL() + "/discord",
                    payload.toString()
            ).body();

            JsonArray result = JsonParser.parseString(emcResponse).getAsJsonArray();
            if (result.isEmpty()) return null;

            JsonObject first = result.get(0).getAsJsonObject();
            return first.get("id").getAsString();

        } catch (Exception e) {
            LOGGER.error("Error while resolving Discord ID for '{}': {}", username, e.getMessage(), e);
            return null;
        }
    }

    private String formatUUID(String uuid) {
        return uuid.substring(0, 8) + "-" +
                uuid.substring(8, 12) + "-" +
                uuid.substring(12, 16) + "-" +
                uuid.substring(16, 20) + "-" +
                uuid.substring(20);
    }

    private boolean isValidUUID(String uuid) {
        return uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
}
