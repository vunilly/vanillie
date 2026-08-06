package com.vunilly.vanillie.twitch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TwitchManager {
    private static HashMap<UUID, String> twitchUsernames = new HashMap<>();
    private static HashMap<UUID, Boolean> statusCache = new HashMap<>();
    private static File dataFile;

    private TwitchManager() {
        // Private constructor to prevent instantiation
    }

    public static void init(File pluginFolder) {
        dataFile = new File(pluginFolder, "twitch_config.yml");
    }

    public static void clearData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        twitchUsernames.clear();
        statusCache.clear();

        config.set("twitch.players", null);
    }

    public static void loadData() {
        if (dataFile == null || !dataFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        if (config.contains("twitch.players")) {
            for (String uuidStr : config.getConfigurationSection("twitch.players").getKeys(false)) {
                String twitchUsername = config.getString("twitch.players." + uuidStr);
                try {
                    UUID playerUUID = UUID.fromString(uuidStr);
                    twitchUsernames.put(playerUUID, twitchUsername);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("[Vanillie] Invalid UUID in twitch config: " + uuidStr);
                }
            }
        }
    }

    public static void saveData() {
        if (dataFile == null) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        for (UUID playerUUID : twitchUsernames.keySet()) {
            String twitchUsername = twitchUsernames.get(playerUUID);
            config.set("twitch.players." + playerUUID.toString(), twitchUsername);
        }

        try {
            config.save(dataFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Failed to save twitch config: " + e.getMessage());
        }
    }

    public static void setTwitchUsername(UUID playerUUID, String twitchUsername) {
        twitchUsernames.put(playerUUID, twitchUsername);
        // Cache invalidieren wenn Username gesetzt wird
        statusCache.remove(playerUUID);
    }

    public static String getTwitchUsername(UUID playerUUID) {
        return twitchUsernames.get(playerUUID);
    }

    /**
     * Cache invalidieren wenn Spieler rejoined
     */
    public static void invalidateCache(UUID playerUUID) {
        statusCache.remove(playerUUID);
    }

    public static boolean isPlayerLiveOnTwitch(UUID playerUUID) {
        String twitchUsername = twitchUsernames.get(playerUUID);

        if (twitchUsername == null || twitchUsername.isEmpty()) {
            return false;
        }

        // Check Cache
        if (statusCache.containsKey(playerUUID)) {
            return statusCache.get(playerUUID);
        }

        try {
            URL url = new URL("https://decapi.me/twitch/uptime/" + twitchUsername);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                connection.disconnect();

                String response = content.toString();

                boolean isLive = !response.contains("is offline") && !response.contains("User not found")
                        && !response.contains("Error");
                
                // Cache speichern
                statusCache.put(playerUUID, isLive);
                
                return isLive;
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen des Twitch-Status für " + twitchUsername + ": " + e.getMessage());
        }

        return false;
    }
}