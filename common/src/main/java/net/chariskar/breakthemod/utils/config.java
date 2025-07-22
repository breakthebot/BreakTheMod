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

package net.chariskar.breakthemod.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class config {
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private static final Gson gson = new Gson();
    public static Boolean dev = false;
    private static config instance = null;
    private static final File configFile = new File(Platform.getConfigFolder().toFile(), "breakthemod_config.json");
    private static String API_URL = "https://api.earthmc.net/v3/aurora";
    private static String MAP_URL = "https://map.earthmc.com/";
    private static String Staff_Repo_Url = "https://raw.githubusercontent.com/jwkerr/staff/master/staff.json";
    public boolean radarEnabled = true;

    // config properties
    private WidgetPosition widgetPosition = WidgetPosition.TOP_LEFT;
    private int customX = 0;
    private int customY = 0;
    private boolean enabledOnOtherServers = false;

    private config() {
        loadConfig();
    }

    public static config getInstance() {
        if (instance == null) {
            instance = new config();
            return instance;
        }
        return instance;
    }

    public File getConfigFile() {
        return configFile;
    }

    public WidgetPosition getWidgetPosition() {
        return widgetPosition;
    }

    public void setWidgetPosition(WidgetPosition position) {
        widgetPosition = position;
    }

    public int getCustomX() {
        return customX;
    }

    public void setCustomX(int x) {
        customX = x;
    }

    public int getCustomY() {
        return customY;
    }

    public void setCustomY(int y) {
        customY = y;
    }

    public boolean getRadarEnabled() {
        return radarEnabled;
    }

    public void setRadarEnabled(boolean enabled) {
        radarEnabled = enabled;
    }

    public boolean isEnabledOnOtherServers() {
        return enabledOnOtherServers;
    }

    public void setEnabledOnOtherServers(boolean enabled) {
        enabledOnOtherServers = enabled;
    }

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean bl) {
        dev = bl;
    }

    public String getApiURL() {
        return formatURL(API_URL);
    }


    public void setApiURL(String url) {
        API_URL = formatURL(url);
    }

    public String getMapURL() {
        return formatURL(MAP_URL);
    }

    public void setMapUrl(String url) {
        MAP_URL = formatURL(url);
    }

    public void setStaffRepoURL(String url) {
        Staff_Repo_Url = formatURL(url);
    }

    public String getStaffRepoURL() {
        return formatURL(Staff_Repo_Url);
    }

    public void saveConfig() {
        JsonObject configJson = new JsonObject();
        configJson.addProperty("widgetPosition", widgetPosition.name());
        configJson.addProperty("customX", customX);
        configJson.addProperty("customY", customY);
        configJson.addProperty("radarEnabled", radarEnabled);
        configJson.addProperty("enabledOnOtherServers", enabledOnOtherServers);
        configJson.addProperty("dev", dev);
        configJson.addProperty("API_URL", API_URL);
        configJson.addProperty("MAP_URL", MAP_URL);
        configJson.addProperty("STAFF_REPO_URL", MAP_URL);

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configJson, writer);
        } catch (IOException e) {
            LOGGER.error("Unexpected I/O exception: {}", e.getMessage());
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
                MAP_URL = configJson.has("MAP_URL") ? configJson.get("MAP_URL").getAsString() : MAP_URL;
                Staff_Repo_Url = configJson.has("STAFF_REPO_URL") ? configJson.get("STAFF_REPO_URL").getAsString() : Staff_Repo_Url;

            } catch (IOException e) {
                LOGGER.error("Unexpected I/O exception: {}", e.getMessage());
            }
        }
    }

    public enum WidgetPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        CUSTOM
    }

    private String formatURL(String url) {
        if (!url.startsWith("https://")) {
            url = "https://" + url;
        }
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }
}