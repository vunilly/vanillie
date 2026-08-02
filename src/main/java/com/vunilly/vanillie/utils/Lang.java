package com.vunilly.vanillie.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lang {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
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

        try (FileReader reader = new FileReader(dataFile, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (root.has("lang")) {
                loadJsonSection(root.getAsJsonObject("lang"), "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadJsonSection(JsonObject obj, String prefix) {
        for (String key : obj.keySet()) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            JsonElement element = obj.get(key);

            if (element.isJsonObject()) {
                loadJsonSection(element.getAsJsonObject(), fullKey);
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                List<String> list = new java.util.ArrayList<>();
                array.forEach(e -> list.add(e.getAsString()));
                LANG.put(fullKey, list);
            } else if (element.isJsonPrimitive()) {
                String value = element.getAsString();
                LANG.put(fullKey, List.of(value));
            }
        }
    }

    public static void saveLang() {
        JsonObject root = new JsonObject();
        JsonObject langObj = new JsonObject();

        for (Map.Entry<String, List<String>> entry : LANG.entrySet()) {
            String[] keyParts = entry.getKey().split("\\.");
            JsonObject current = langObj;

            // Navigiere durch die Hierarchie und erstelle fehlende Objects
            for (int i = 0; i < keyParts.length - 1; i++) {
                if (!current.has(keyParts[i])) {
                    current.add(keyParts[i], new JsonObject());
                }
                current = current.getAsJsonObject(keyParts[i]);
            }

            // Setze den finalen Wert
            String lastKey = keyParts[keyParts.length - 1];
            if (entry.getValue().size() == 1) {
                current.addProperty(lastKey, entry.getValue().get(0));
            } else {
                JsonArray array = new JsonArray();
                entry.getValue().forEach(array::add);
                current.add(lastKey, array);
            }
        }

        root.add("lang", langObj);

        try (FileWriter writer = new FileWriter(dataFile, StandardCharsets.UTF_8)) {
            writer.write(GSON.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(JavaPlugin plugin) {
        File pluginFolder = plugin.getDataFolder();

        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        dataFile = new File(pluginFolder, "lang.json");

        if (!dataFile.exists()) {
            plugin.saveResource("lang.json", false);
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
        LANG.put(key, List.of(value));
        saveLang();
    }

    public static Component getComponent(String key, TagResolver... resolvers) {
        List<Component> list = get(key, resolvers);
        return list.isEmpty() ? Component.empty() : list.getFirst();
    }
}