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

package net.charisk.breakthemod.Fetch;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fetch {
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    /**
     * Sends an HTTP POST request with the provided URL and JSON payload and returns the response as a String.
     * @param url the URL to send the request to
     * @param payload the JSON payload to include in the request body
     * @return the response body as a String
     * @throws Exception It covers all possible exceptions
     */
    public String PostRequest(String url, String payload) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload));
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }



    /**
     * Sends an HTTP GET request to the provided URL and returns the response as a String.
     * @param url the URL to send the request to
     * @return the response body as a String
     * @throws Exception It covers all possible exceptions
     */
    public String GetRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
        requestBuilder.GET();
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
