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

package net.chariskar.breakthemod.api;

import com.google.gson.*;
import net.chariskar.breakthemod.types.Nation;
import net.chariskar.breakthemod.types.Resident;
import net.chariskar.breakthemod.types.Town;
import net.chariskar.breakthemod.types.reference;
import net.chariskar.breakthemod.utils.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * This is the high level fetch class, if you want the fetch types, you need to go to {@link net.chariskar.breakthemod.types}
 */
public class Fetch {
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private static final Fetch INSTANCE = new Fetch(); // singleton instance
    private final Gson gson = new Gson();

    private Fetch() {
    }

    public static Fetch getInstance() {
        return INSTANCE;
    }

    // specific town/nation/player getters.

    public Town getTown(String name) {
        return getTowns(name).getFirst();
    }

    public List<Town> getTowns(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }
        return getObjects("towns", names, Town.class);
    }

    public List<Town> getTowns(String... names) {
        return getTowns(Arrays.asList(names));
    }

    public Nation getNation(String name) {
        return getNations(name).getFirst();
    }

    public List<Nation> getNations(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }
        return getObjects("nations", names, Nation.class);
    }

    public List<Nation> getNations(String... names) {
        return getNations(Arrays.asList(names));
    }

    public Resident getResident(String name) {
        return getResidents(Collections.singletonList(name)).getFirst();
    }

    public List<Resident> getResidents(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        return getObjects("players", names, Resident.class);
    }

    // all getters.

    public List<reference> getAllTowns() {
        return getAll("towns", reference.class);
    }

    public List<reference> getAllNations() {
        return getAll("nations", reference.class);
    }

    public List<reference> getAllPlayers() {
        return getAll("players", reference.class);
    }

    // internal methods

    /**
     * GET API interaction function.
     *
     * @param endpoint Which api endpoint is the data located in.
     * @param <T>      The class in which to parse the data in.
     */
    public <T> List<T> getAll(String endpoint, Class<T> T) {
        try {
            HttpResponse<String> rawResponse = GetRequest(config.getInstance().getApiURL() + endpoint);
            if (rawResponse.statusCode() != 200 | rawResponse.body().isEmpty()) return null;
            String response = rawResponse.body();
            JsonArray responseArray = JsonParser.parseString(response).getAsJsonArray();
            List<T> results = new ArrayList<>();

            for (JsonElement element : responseArray) {
                T obj = gson.fromJson(element.getAsJsonObject(), T);
                results.add(obj);
            }
            return results;
        } catch (Exception e) {
            logError("Unexpected error occurred while trying to access web resource", e);
            return null;
        }
    }

    /**
     * POST API interaction function.
     *
     * @param endpoint Which api endpoint is the data located in.
     * @param names    The names of the items.
     * @param <T>      The class in which to parse the data in.
     */
    public <T> List<T> getObjects(
            String endpoint,
            List<String> names,
            Class<T> type
    ) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        JsonObject payload = new JsonObject();
        JsonArray query = new JsonArray();
        names.forEach(query::add);
        payload.add("query", query);

        try {
            HttpResponse<String> rawResponse = PostRequest(
                    config.getInstance().getApiURL() + endpoint,
                    payload.toString()
            );

            if (rawResponse.statusCode() != 200) {
                LOGGER.error("Unexpected error occurred while sending request. status code");

                return null;
            }

            String response = rawResponse.body();

            if (response.isEmpty()) {
                return Collections.emptyList();
            }

            JsonArray responseArray = JsonParser.parseString(response).getAsJsonArray();
            LOGGER.debug("Response array recieved {}", responseArray);

            if (responseArray.isEmpty()) {
                return null;
            }

            List<T> results = new ArrayList<>();
            for (JsonElement element : responseArray) {
                try {
                    T obj = gson.fromJson(element.getAsJsonObject(), type);
                    results.add(obj);
                } catch (Exception e) {
                    LOGGER.warn("Failed to parse object of type {} from response: {}", type.getSimpleName(), e.getMessage());
                }
            }
            return results;
        } catch (Exception e) {
            logError("Unexpected error occurred while fetching", e);
        }
        return new ArrayList<>();
    }

    private void logError(String message, Exception e) {
        LOGGER.error("{}{}", message, e.getMessage());
        if (config.getInstance().isDev()) {
            e.printStackTrace();
        }
    }

    /**
     * @param url     the URL to send the request to
     * @param payload the JSON payload to include in the request body
     * @return the response as {@link java.net.http.HttpResponse}
     * @throws Exception It covers all possible exceptions
     */
    public HttpResponse<String> PostRequest(String url, String payload) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload));
        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    /**
     * @param url the URL to send the request to
     * @return the response as {@link java.net.http.HttpResponse}
     * @throws Exception It covers all possible exceptions
     */
    public HttpResponse<String> GetRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
        requestBuilder.GET();
        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
