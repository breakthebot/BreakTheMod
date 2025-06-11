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


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.charisk.breakthemodrewrite.Fetch.fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class onlinestaffService {
    private fetch fetcher = new fetch();

    public List<UUID> get(List<UUID> onlineUsers) throws Exception {
        String jsonResponse = fetcher.GetRequest("https://raw.githubusercontent.com/jwkerr/staff/master/staff.json");
        JsonObject staffJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        List<String> staffUuids = new ArrayList<>();

        for (String role : staffJson.keySet()) {
            JsonArray roleArray = staffJson.getAsJsonArray(role);
            for (JsonElement element : roleArray) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    staffUuids.add(element.getAsString());
                }
            }
        }
        return onlineUsers.stream()
                .map(UUID::toString)
                .filter(staffUuids::contains)
                .map(UUID::fromString)
                .toList();
    }
}
