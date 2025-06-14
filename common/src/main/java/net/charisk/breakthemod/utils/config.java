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

package net.charisk.breakthemod.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import java.io.*;

public class config {
    private static config instance = null;

    private WidgetPosition widgetPosition = WidgetPosition.TOP_LEFT;
    private int customX = 0;
    private int customY = 0;
    public boolean radarEnabled = true;
    private boolean enabledOnOtherServers = false;
    private static final File configFile = new File(Platform.getConfigFolder().toFile(), "breakthemod_config.json");

    private static final Gson gson = new Gson();
    public static Boolean dev = false;
    public static String API_URL = "https://api.earthmc.net/";
    private config() {
        loadConfig();
    }

    public static config getInstance() {
        if (instance == null) {
            instance = new config();
        }
        return instance;
    }


    public enum WidgetPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        CUSTOM
    }

    public WidgetPosition getWidgetPosition() { return widgetPosition; }
    public void setWidgetPosition(WidgetPosition position) { widgetPosition = position; }

    public int getCustomX() { return customX; }
    public void setCustomX(int x) { customX = x; }

    public int getCustomY() { return customY; }
    public void setCustomY(int y) { customY = y; }

    public boolean getRadarEnabled() { return radarEnabled; }
    public void setRadarEnabled(boolean enabled) { radarEnabled = enabled; }

    public boolean isEnabledOnOtherServers() { return enabledOnOtherServers; }
    public void setEnabledOnOtherServers(boolean enabled) { enabledOnOtherServers = enabled; }

    public boolean isDev() { return dev;}
    public void setDev(boolean bl) { dev = bl; }

    public void saveConfig() {
        JsonObject configJson = new JsonObject();
        configJson.addProperty("widgetPosition", widgetPosition.name());
        configJson.addProperty("customX", customX);
        configJson.addProperty("customY", customY);
        configJson.addProperty("radarEnabled", radarEnabled);
        configJson.addProperty("enabledOnOtherServers", enabledOnOtherServers);
        configJson.addProperty("dev", dev);
        configJson.addProperty("API_URL", API_URL);
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject configJson = gson.fromJson(reader, JsonObject.class);
                widgetPosition = configJson.has("widgetPosition")
                        ? WidgetPosition.valueOf(configJson.get("widgetPosition").getAsString())
                        : WidgetPosition.TOP_LEFT;
                customX = configJson.has("customX") ? configJson.get("customX").getAsInt() : 0;
                customY = configJson.has("customY") ? configJson.get("customY").getAsInt() : 0;
                radarEnabled = !configJson.has("radarEnabled") || configJson.get("radarEnabled").getAsBoolean();
                enabledOnOtherServers = configJson.has("enabledOnOtherServers") && configJson.get("enabledOnOtherServers").getAsBoolean();
                dev = configJson.has("dev") ? configJson.get("dev").getAsBoolean() : dev;
                API_URL = configJson.has("API_URL") ? configJson.get("API_URL").getAsString() : API_URL;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}