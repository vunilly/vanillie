package com.vunilly.vanillie.settings;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {
    private static File dataFile;

    private SettingsManager() {

    }

    public static void init(File pluginFolder) {
        dataFile = new File(pluginFolder, "main_config.yml");
    }

    private static void clearData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        config.set("settings", null);
    }

    public static void loadData() {
        if (dataFile == null || !dataFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        if (config.contains("settings")) {

        }
    }

    public static void saveData() {
        if (dataFile == null) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        config.set("test", 0);

        try {
            config.save(dataFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Failed to save settings Main config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
