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

import com.google.gson.*;
import net.chariskar.breakthemod.utils.config;

import java.util.Optional;

public class coordsService extends Service {
    private final Gson gson = new Gson();

    public LocationResult get(double x, double z) throws Exception {
        JsonArray coords = new JsonArray();
        coords.add(x);
        coords.add(z);
        JsonArray queryArray = new JsonArray();
        queryArray.add(coords);
        JsonObject payload = new JsonObject();
        payload.add("query", queryArray);

        String resp = fetch.PostRequest(config.getInstance().getApiURL() + "location", payload.toString()).body();

        JsonArray data = JsonParser.parseString(resp).getAsJsonArray();
        if (data.size() != 1 || !data.get(0).isJsonObject()) {
            throw new JsonParseException("Unexpected API response format: " + resp);
        }

        JsonObject obj = data.get(0).getAsJsonObject();
        boolean isWilderness = obj.has("isWilderness") && obj.get("isWilderness").getAsBoolean();

        Optional<String> townName = Optional.empty();
        Optional<String> nationName = Optional.empty();
        if (!isWilderness) {
            if (obj.has("town") && obj.get("town").isJsonObject()) {
                JsonObject town = obj.getAsJsonObject("town");
                if (town.has("name")) townName = Optional.of(town.get("name").getAsString());
            }
            if (obj.has("nation") && obj.get("nation").isJsonObject()) {
                JsonObject nation = obj.getAsJsonObject("nation");
                if (nation.has("name")) nationName = Optional.of(nation.get("name").getAsString());
            }
        }

        return new LocationResult(isWilderness, townName, nationName);
    }

    public record LocationResult(boolean wilderness, Optional<String> townName, Optional<String> nationName) {

        public String toString() {
                return "LocationResult{" +
                        "wilderness=" + wilderness +
                        ", townName=" + townName +
                        ", nationName=" + nationName +
                        '}';

        }

        public String format() {
            StringBuilder sb = new StringBuilder();

            if (wilderness) {
                sb.append("Coordinates are in the wilderness.");
            } else {
                sb.append("Coordinates are in ").append(townName.orElse("not in a town"));

                nationName.ifPresent(s -> sb.append(", part of the nation of ").append(s));

                sb.append(".");
            }

            return sb.toString();
        }
    }

}
