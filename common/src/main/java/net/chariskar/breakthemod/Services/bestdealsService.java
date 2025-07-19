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


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.chariskar.breakthemod.api.Fetch;
import net.chariskar.breakthemod.utils.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bestdealsService {
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private static final Pattern PARSER = Pattern.compile(
            "Shop\\{id=(\\d+), owner='([^']+)', item='([^']+)', price=([\\d.]+), type='([^']+)', space=(\\d+), stock=(-?\\d+)\\}"
    );
    private final Gson gson = new Gson();
    private Fetch fetchInstance = Fetch.getInstance();

    public List<Shop> get(String itemName, String nationName) {
        //Nation nation = new Fetch().getNation(nationName);
        List<Shop> shops = Collections.emptyList();

        try {
            shops = new ArrayList<>(
                    parseShops(
                            JsonParser
                                    .parseString(
                                            fetchInstance.GetRequest(
                                                    config.getInstance().getApiURL() + "/shops"
                                            ).body()
                                    )
                                    .getAsJsonArray()
                    )
            );


            // Set<UUID> nationEnemies = nation.enemies.stream()
            //         .map(r -> r.getUuid().get())
            //         .collect(Collectors.toSet());

            // Set<UUID> sanctioned = nation.sanctioned.stream()
            //         .map(r -> r.getUuid().get())
            //         .collect(Collectors.toSet());

            // shops.removeIf(s -> nationEnemies.contains(s.getOwner()));
            //shops.removeIf(s -> sanctioned.contains(s.getOwner()));
            shops.removeIf(s -> s.getType().equalsIgnoreCase("SELLING"));
            shops.removeIf(s -> !s.matches(itemName));

            shops.sort(Comparator.comparingDouble(Shop::getPrice));

        } catch (Exception e) {
            if (config.getInstance().isDev()) {
                e.printStackTrace();
            } else {
                LOGGER.error("Unexpected Error Occurred", e);
            }
        }
        return shops;
    }

    public List<Shop> parseShops(JsonArray resp) {
        return resp.asList().stream().map(raw -> Shop.fromString(raw.getAsString())).toList();
    }

    public static class Shop {
        private final int id;
        private final UUID owner;
        private final String item;
        private final double price;
        private final String type;
        private final int space;
        private final int stock;

        public Shop(int id, UUID owner, String item, double price, String type, int space, int stock) {
            this.id = id;
            this.owner = owner;
            this.item = item;
            this.price = price;
            this.type = type;
            this.space = space;
            this.stock = stock;
        }

        /**
         * Parses a Shop from the exact format produced by toString().
         *
         * @param input the String to parse.
         * @return a new Shop instance
         * @throws IllegalArgumentException if the input doesn’t match
         */
        public static Shop fromString(String input) {
            Matcher m = PARSER.matcher(input.trim());
            if (!m.matches()) {
                throw new IllegalArgumentException("Invalid Shop string: " + input);
            }
            int id = Integer.parseInt(m.group(1));
            UUID owner = UUID.fromString(m.group(2));
            String item = m.group(3);
            double price = Double.parseDouble(m.group(4));
            String type = m.group(5);
            int space = Integer.parseInt(m.group(6));
            int stock = Integer.parseInt(m.group(7));
            return new Shop(id, owner, item, price, type, space, stock);
        }

        public double getPrice() {
            return price;
        }

        public UUID getOwner() {
            return owner;
        }

        public String getType() {
            return type;
        }

        public boolean matches(String query) {
            query = query.toLowerCase();
            return
                    String.valueOf(id).contains(query) ||
                            item.toLowerCase().contains(query) ||
                            String.valueOf(price).contains(query) ||
                            type.toLowerCase().contains(query) ||
                            String.valueOf(space).contains(query) ||
                            String.valueOf(stock).contains(query);
        }

        @Override
        public String toString() {
            return String.format(
                    "Shop{id=%d, owner='%s', item='%s', price=%f, type='%s', space=%d, stock=%d}",
                    id, owner, item, price, type, space, stock
            );
        }

    }


}
