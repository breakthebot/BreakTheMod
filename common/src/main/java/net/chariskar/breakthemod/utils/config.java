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
import com.google.gson.JsonSyntaxException;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;

public class config {
    private static final Logger LOGGER = LoggerFactory.getLogger("breakthemod");
    private static final Gson gson = new Gson();
    public static Boolean dev = false;
    private static config instance = null;
    private static final File configFile = new File(Platform.getConfigFolder().toFile(), "breakthemod_config.json");
    private static String API_URL = "https://api.earthmc.net/v3/aurora";
    private static String MAP_URL = "https://map.earthmc.net/";
    private static String BTM_VERSION = "1.2.3";
    private static String Staff_Repo_Url = "https://raw.githubusercontent.com/jwkerr/staff/master/staff.json";
    public boolean radarEnabled = true;

    private WidgetPosition widgetPosition = WidgetPosition.TOP_LEFT;
    private int customX = 0;
    private int customY = 0;
    private boolean enabledOnOtherServers = false;

    public static  class ConfigData {
        public boolean radarEnabled;
        public config.WidgetPosition widgetPosition;
        public int customX;
        public int customY;
        public boolean enabledOnOtherServers;
        public boolean devMode;
        public String apiURL;
        public String mapURL;
        public String staffRepoURL;
        public String btmVersion;

        public ConfigData() {}

        public ConfigData(
                boolean radarEnabled,
                config.WidgetPosition widgetPosition,
                int customX,
                int customY,
                boolean enabledOnOtherServers,
                boolean devMode,
                String apiURL,
                String mapURL,
                String staffRepoURL,
                String btmVersion
        ) {
            this.radarEnabled = radarEnabled;
            this.widgetPosition = widgetPosition;
            this.customX = customX;
            this.customY = customY;
            this.enabledOnOtherServers = enabledOnOtherServers;
            this.devMode = devMode;
            this.apiURL = apiURL;
            this.mapURL = mapURL;
            this.staffRepoURL = staffRepoURL;
            this.btmVersion = btmVersion;
        }
    }

    private config(ConfigData data) {
        this.radarEnabled = data.radarEnabled;
        this.widgetPosition = data.widgetPosition;
        this.customX = data.customX;
        this.customY = data.customY;
        this.enabledOnOtherServers = data.enabledOnOtherServers;
        dev = data.devMode;
        BTM_VERSION = data.btmVersion;
        API_URL = data.apiURL;
        MAP_URL = data.mapURL;
        Staff_Repo_Url = data.staffRepoURL;
    }

    public static config getInstance() {
        if (instance == null) {
            instance = new config(load_config().get());
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

    public String getBtmVersion() {return BTM_VERSION;}

    public void setBtmVersion(String version) { BTM_VERSION = version; }


    public static Optional<ConfigData> load_config() {
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                ConfigData defaultConfig = new ConfigData(
                        true,
                        WidgetPosition.TOP_LEFT,
                        0,
                        0,
                        false,
                        false,
                        API_URL,
                        MAP_URL,
                        Staff_Repo_Url,
                        BTM_VERSION
                );
                try (FileWriter writer = new FileWriter(configFile)) {
                    gson.toJson(defaultConfig, writer);
                }
                return Optional.of(defaultConfig);
            } catch (IOException e) {
                logError("Unable to create default config file", e);
                return Optional.empty();
            }
        }

        try (FileReader file = new FileReader(configFile)) {
            ConfigData data = gson.fromJson(file, ConfigData.class);
            return Optional.ofNullable(data);
        } catch (IOException e) {
            logError("I/O exception when reading file", e);
        } catch (JsonSyntaxException e) {
            logError("Unable to parse config, regenerating with defaults", e);
            return regenerateDefaultConfig();
        }

        return Optional.empty();
    }

    private static Optional<ConfigData> regenerateDefaultConfig() {
        try {
            ConfigData defaultConfig = new ConfigData(
                    true,
                    WidgetPosition.TOP_LEFT,
                    0,
                    0,
                    false,
                    false,
                    API_URL,
                    MAP_URL,
                    Staff_Repo_Url,
                    BTM_VERSION
            );
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(defaultConfig, writer);
            }
            return Optional.of(defaultConfig);
        } catch (IOException ex) {
            logError("Unable to regenerate default config file", ex);
            return Optional.empty();
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

    public void saveConfig() {
        ConfigData data = new ConfigData(
                this.radarEnabled,
                this.widgetPosition,
                this.customX,
                this.customY,
                this.enabledOnOtherServers,
                dev,
                API_URL,
                MAP_URL,
                Staff_Repo_Url,
                BTM_VERSION
        );

        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(data, writer);
            }
            LOGGER.info("Config saved to {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            logError("Unable to save config", e);
        }
    }


    protected static void logError(String message, Exception e) {
        LOGGER.error("{}{}", message, e.getMessage());
        if (config.getInstance().isDev()) {
            LOGGER.error("{}", Arrays.toString(e.getStackTrace()));
        }
    }
}