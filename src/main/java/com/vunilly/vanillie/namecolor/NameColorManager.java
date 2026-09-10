package com.vunilly.vanillie.namecolor;

import com.vunilly.vanillie.display.NametagListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NameColorManager {
    private static final Map<UUID, String> playerColors = new ConcurrentHashMap<>();
    private static File dataFile;

    // Private Constructor (Utility-Class Pattern)
    private NameColorManager() {
    }

    public static void init(File pluginFolder) {
        dataFile = new File(pluginFolder, "namecolor_config.yml");
    }

    public static void clearData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        config.set("players", null);

        playerColors.clear();
    }

    public static void loadData() {
        if (dataFile == null || !dataFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        Map<UUID, String> saved = (Map<UUID, String>) config.getMapList("players");
    }

    public static void saveData() {
        if (dataFile == null) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("players", playerColors);

        try {
            config.save(dataFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Failed to save Player Colors config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void resetColor(UUID uuid) {
        setColor(uuid, "#ffffff");
    }

    public static void setColor(UUID uuid, String color) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        playerColors.put(uuid, color);
        NametagListener.updateNametag(player);
    }

    public static String getColor(UUID uuid) {
        String color = playerColors.get(uuid);
        return Objects.requireNonNullElse(color, "#ffffff");
    }
}
