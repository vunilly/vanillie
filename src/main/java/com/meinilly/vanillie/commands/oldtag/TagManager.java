package com.meinilly.vanillie.commands.oldtag;

import java.io.File;
import java.nio.file.Files;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.meinilly.vanillie.commands.oldtag.Tag;

public class TagManager {
    private static final List<Tag> serverTagList = Collections.synchronizedList(new ArrayList<>());
    private static final Map<UUID, List<Integer>> playerUsedTags = Collections.synchronizedMap(new HashMap<>());
    private static File dataFile;

    public static void loadTags() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        if (config.contains("tags")) {
            for (String key : config.getConfigurationSection("tags").getKeys(false)) {
                int id = config.getInt("tags." + key + ".id");
                String uuidString = config.getString("tags." + key + ".owner");
                UUID ownerUuid = UUID.fromString(uuidString);
                String tagText = config.getString("tags." + key + ".tag");

                Tag tag = new Tag(id, ownerUuid, tagText);
                serverTagList.add(tag);
            }
        }

        if (config.contains("players")) {
            for (String key : config.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                List<Integer> ids = (List<Integer>) config.getList("players." + uuid);

                playerUsedTags.put(uuid, ids);
            }
        }
    }

    public static void saveTags(File pluginFolder) {
        FileConfiguration config = new YamlConfiguration();

        int i = 0;
        for (Tag tag : serverTagList) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
            String username = offlinePlayer.getName();

            config.set("tags." + i + ".id", tag.getId());
            config.set("tags." + i + ".owner", tag.getOwnerUUID().toString());
            config.set("tags." + i + ".tag", tag.getTagString());
            config.set("tags." + i + ".owner_name", username);
            i++;
        }

        for (UUID uuid : playerUsedTags.keySet()) {
            List<Integer> ids = playerUsedTags.get(uuid);
            config.set("players." + uuid, ids);
        }

        try {
            config.save(dataFile);

            String content = new String(Files.readAllBytes(dataFile.toPath()));
            String withComment = "# Vanillie Plugin Tag Config\n# DO NOT EDIT\n" + content;
            Files.write(dataFile.toPath(), withComment.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(File pluginFolder) {
        dataFile = new File(pluginFolder, "tag_config.yml");
    }

    public static synchronized String newTag(UUID uuid, String tag) {
        int newId = findLowestAvailableId();

        serverTagList.add(new Tag(newId, uuid, tag));

        addTag(uuid, String.valueOf(newId));
        return "Der Tag wurde erfolgreich erstellt.";
    }

    private static int findLowestAvailableId() {
        // Alle IDs die schon existieren sammeln
        Set<Integer> usedIds = serverTagList.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        // Die niedrigste freie ID finden
        int id = 0;
        while (usedIds.contains(id)) {
            id++;
        }
        return id;
    }

    public static synchronized String addTag(UUID uuid, String stringId) {
        if (findPlayerTagCount(uuid) > 30) {
            return "ONG Warum willst du mehr als 30 Tags. es <b>REICHT!</b>";
        }

        if (!isInteger(stringId)) {
            return "Du hast keine Zahl eingegeben.";
        }
        Integer id = Integer.parseInt(stringId);

        List<Integer> playerTags = playerUsedTags.get(uuid);
        if (playerTags != null) {
            for (int tagId : playerTags) {
                if (tagId == id) {
                    return "Du hast diesen Tag schon.";
                }
            }
        }

        if (findTagById(id) != null) {
            playerUsedTags.computeIfAbsent(uuid, key -> new ArrayList<>()).add(id);

            return "Der Tag wurde erfolgreich aktiviert.";
        } else {
            return "Ein Tag mit dieser Nummer existiert nicht.";
        }
    }

    public static synchronized String removeTag(UUID uuid, String stringId) {
        if (!isInteger(stringId)) {
            return "Du hast keine Zahl eingegeben.";
        }
        Integer id = Integer.parseInt(stringId);

        List<Integer> tags = playerUsedTags.get(uuid);
        if (tags != null) {
            if (tags != null && tags.remove(Integer.valueOf(id))) {
                return "Der Tag wurde erfolgreich deaktiviert.";
            } else {
                return "Ein Tag mit dieser Nummer ist nicht für dich aktiviert.";
            }
        } else {
            return "Deine Liste konnte nicht abgerufen werden (Notiz von der Programmierin: Das sollte nie passieren!!!)";
        }
    }

    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static synchronized String deleteTagFromList(UUID uuid, String stringId) {
        if (!isInteger(stringId)) {
            return "Du hast keine Zahl eingegeben.";
        }
        Integer id = Integer.parseInt(stringId);

        Tag toDeleteTag = findTagById(id);

        if (toDeleteTag != null) {
            if (toDeleteTag.getOwnerUUID().equals(uuid)) {
                serverTagList.remove(toDeleteTag);
                return "Der Tag wurde erfolgreich gelöscht.";
            } else {
                return "Du kannst nur die Tags löschen die du erstellt hast!";
            }
        } else {
            return "Ein Tag mit dieser Nummer existiert nicht.";
        }
    }

    public static String getTagList() {
        StringBuilder result = new StringBuilder();

        for (Tag tag : serverTagList) {
            result.append(tag.getId() + " - " + tag.getTagString() + " von ");
            result.append(tag.getOwnerUUID());
            result.append("\\n<reset><color:#c4abff>");
        }
        if (serverTagList.isEmpty()) {
            result.append("Keine Einträge gefunden! Nutze <b>/tag new</b> um einen Tag zu erstellen.");
        }

        return "Alle Tags <Nummer, Tag>:\n" + result.toString() + "<gradient:red:green>";
    }

    public static synchronized String getTag(UUID uuid) {
        StringBuilder result = new StringBuilder();

        List<Integer> playerTagList = playerUsedTags.get(uuid);

        if (playerTagList == null || playerTagList.isEmpty()) {
            return "";
        }

        playerTagList.forEach(tagId -> {
            Tag tag = findTagById(tagId);

            if (tag != null) {
                result.append(tag.getTagString());
            }
        });

        return result.toString();
    }

    // public static synchronized String moveUp()

    private static synchronized Tag findTagById(int id) {
        for (Tag tag : serverTagList) {
            if (tag.getId() == id) {
                return tag;
            }
        }
        return null;
    }

    public static synchronized int findPlayerTagCount(UUID playerId) {
        int count = 0;
        for (Tag tag : serverTagList) {
            if (tag.getOwnerUUID() == playerId) {
                count++;
            }
        }
        return count;
    }

    public static synchronized int findTagPlayerCount(int tagId) {
        int count = 0;
        for (Tag tag : serverTagList) {
            if (tag.getId() == tagId) {
                count++;
            }
        }
        return count;
    }

    public static synchronized boolean doesPlayerHaveTag(UUID playerUuid, int tagId) {
        List<Integer> playerTagList = playerUsedTags.get(playerUuid);

        for (int playersTag : playerTagList) {
            if (playersTag == tagId) {
                return true;
            }
        }
        return false;
    }

    public static synchronized int getPlayerCreatedTagCount(UUID playerUuid) {
        int count = 0;
        for (Tag tag : serverTagList) {
            if (tag.getOwnerUUID().equals(playerUuid)) {
                count++;
            }
        }
        return count;
    }

    public static synchronized List<Tag> getActiveTagsForPlayer(UUID playerUuid) {
        List<Tag> tagListForPlayer = Collections.synchronizedList(new ArrayList<>());

        for (int tagId : playerUsedTags.get(playerUuid)) {
            Tag foundTag = findTagById(tagId);
            if (foundTag != null) {
                tagListForPlayer.add(foundTag);
            }
        }
        return tagListForPlayer;
    }

    public static synchronized List<Tag> getCreatedTagsForPlayer(UUID playerUuid) {
        List<Tag> tagListForPlayer = Collections.synchronizedList(new ArrayList<>());

        for (Tag tag : serverTagList) {
            if (tag.owner_uuid.equals(playerUuid)) {
                tagListForPlayer.add(tag);
            }
        }
        return tagListForPlayer;
    }

    public static synchronized List<Tag> getServerTagList() {
        return serverTagList;
    }

    public static synchronized Map<UUID, List<Integer>> getPlayerUsedTags() {
        return playerUsedTags;
    }

    public static synchronized void clearAll() {
        serverTagList.clear();
        playerUsedTags.clear();
    }
}
