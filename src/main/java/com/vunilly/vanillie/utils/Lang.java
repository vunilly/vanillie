package com.vunilly.vanillie.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final Map<String, List<String>> LANG = new LinkedHashMap<>();
    private static File dataFile;

    public static void clearData() {
        LANG.clear();
    }

    public static void loadLang() {
        LANG.clear();

        if (!dataFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        if (!config.contains("lang")) {
            return;
        }

        for (String key : config.getConfigurationSection("lang").getKeys(true)) {

            if (config.isConfigurationSection("lang." + key)) {
                continue;
            }

            if (config.isList("lang." + key)) {
                LANG.put(key, config.getStringList("lang." + key));
            } else {
                LANG.put(key, List.of(config.getString("lang." + key)));
            }
        }
    }

    public static void saveLang() {
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, List<String>> entry : LANG.entrySet()) {
            if (entry.getValue().size() == 1) {
                config.set("lang." + entry.getKey(), entry.getValue().get(0));
            } else {
                config.set("lang." + entry.getKey(), entry.getValue());
            }
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(File pluginFolder) {
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        dataFile = new File(pluginFolder, "lang_config.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void add(String key, String text) {
        LANG.put(key, Arrays.asList(text.split("\\n|", -1)));
    }

    public static List<Component> get(String key, TagResolver... resolvers) {
        List<String> lines = LANG.get(key);

        if (lines == null) {
            return List.of(MINI_MESSAGE.deserialize(key));
        }

        return lines.stream()
                .map(line -> MINI_MESSAGE.deserialize(line, resolvers))
                .toList();
    }

    public static String getString(String key) {
        List<String> lines = LANG.get(key);

        if (lines == null) {
            return key;
        }

        return lines.getFirst();
    }

    private static void saveMissingKey(String key, String value) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("lang." + key, value);
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Component getComponent(String key, TagResolver... resolvers) {
        List<Component> list = get(key, resolvers);
        return list.isEmpty() ? Component.empty() : list.getFirst();
    }
}