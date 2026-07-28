package com.meinilly.vanillie.tag;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.meinilly.vanillie.tag.Tag;
import com.meinilly.vanillie.utils.Lang;

import net.kyori.adventure.text.Component;

public class TagManager {
    private static final List<Tag> allTags = Collections.synchronizedList(new ArrayList<>());
    private static final Map<UUID, List<Integer>> usedTags = new ConcurrentHashMap<>();
    private static File dataFile;

    public static void loadData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        if (config.contains("tags")) {
            for (String key : config.getConfigurationSection("tags").getKeys(false)) {
                int id = config.getInt("tags." + key + ".id");
                String uuidString = config.getString("tags." + key + ".owner");
                UUID ownerUuid = UUID.fromString(uuidString);
                String tagText = config.getString("tags." + key + ".tag");

                Tag tag = new Tag(id, ownerUuid, tagText);
                allTags.add(tag);
            }
        }

        if (config.contains("players")) {
            for (String key : config.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                List<Integer> ids = (List<Integer>) config.getList("players." + uuid);

                usedTags.put(uuid, ids);
            }
        }
    }

    public static void saveData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        int i = 0;
        for (Tag tag : allTags) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
            String username = offlinePlayer.getName();

            config.set("tags." + i + ".id", tag.getId());
            config.set("tags." + i + ".owner", tag.getOwnerUUID().toString());
            config.set("tags." + i + ".tag", tag.getTagString());
            config.set("tags." + i + ".owner_name", username);
            i++;
        }

        for (UUID uuid : usedTags.keySet()) {
            List<Integer> ids = usedTags.get(uuid);
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

    public void setup(File pluginFolder) {
        dataFile = new File(pluginFolder, "tag_config.yml");
    }

    public static synchronized List<Tag> getAllTags() {
        return allTags;
    }

    public static synchronized Map<UUID, List<Integer>> getUsedTags() {
        return usedTags;
    }

    private static synchronized int findNewId() {
        int id = 1;

        while (true) {
            boolean exists = false;

            for (Tag tag : allTags) {
                if (tag.getId() == id) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                return id;
            }

            id++;
        }
    }

    public static synchronized Component createTag(UUID ownerUuid, String text) {
        if (doesTagAlreadyExist(text))
            return Lang.get("msg.tag.alreadyExists").getFirst();

        int newId = findNewId();

        Tag newTag = new Tag(newId, ownerUuid, text);

        allTags.add(newTag);

        return Lang.get("msg.tag.created").getFirst();
    }

    public static synchronized Component deleteTag(UUID ownerUuid, int tagId, boolean isOp) {
        if (!doesTagAlreadyExist(tagId))
            return Lang.get("msg.tag.invalidId").getFirst();

        // sollte nie null sein, weil wir es oben gecheckt haben
        Tag toDeleteTag = getTagById(tagId);
        if (toDeleteTag == null) {
            throw new IllegalStateException("Tag exists in allTags list, but somehow doesnt! This cant happen.");
        }

        if (!toDeleteTag.getOwnerUUID().equals(ownerUuid))
            return Lang.get("msg.tag.notYours").getFirst();

        // deactivate from all
        for (UUID playerUuid : usedTags.keySet()) {
            if (doesPlayerHaveTag(playerUuid, tagId)) {
                deactivateTag(playerUuid, tagId);
            }
        }

        allTags.remove(toDeleteTag);

        return Lang.get("msg.tag.deleted").getFirst();
    }

    public static synchronized Component activateTag(UUID addToWho, int tagId) {
        if (doesPlayerHaveTag(addToWho, tagId))
            return Lang.get("msg.tag.alreadyActivated").getFirst();
        if (!isTagIdValid(tagId))
            return Lang.get("msg.tag.invalidId").getFirst();

        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(addToWho);

        tagsOfPlayer.add(tagId);

        return Lang.get("msg.tag.activated").getFirst();
    }

    public static synchronized Component deactivateTag(UUID removeFromWho, int tagId) {
        if (!doesPlayerHaveTag(removeFromWho, tagId))
            return Lang.get("msg.tag.alreadyDeactivated").getFirst();
        if (!isTagIdValid(tagId))
            return Lang.get("msg.tag.invalidId").getFirst();

        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(removeFromWho);

        tagsOfPlayer.remove(Integer.valueOf(tagId));

        return Lang.get("msg.tag.deactivated").getFirst();
    }

    public static synchronized Component moveTagUp(UUID playerUuid, int tagIndex) {
        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(playerUuid);

        if (tagIndex <= 0 || tagIndex >= tagsOfPlayer.size()) {
            return Lang.get("msg.tag.cannotMove").getFirst();
        }

        Collections.swap(tagsOfPlayer, tagIndex, tagIndex - 1);

        return Lang.get("msg.tag.moved").getFirst();
    }

    public static synchronized Component moveTagDown(UUID playerUuid, int tagIndex) {
        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(playerUuid);

        if (tagIndex < 0 || tagIndex >= tagsOfPlayer.size() - 1) {
            return Lang.get("msg.tag.cannotMove").getFirst();
        }

        Collections.swap(tagsOfPlayer, tagIndex, tagIndex + 1);

        return Lang.get("msg.tag.moved").getFirst();
    }

    private static synchronized Tag getTagById(int tagId) {
        for (Tag tag : allTags) {
            if (tag.getId() == tagId) {
                return tag;
            }
        }
        return null;
    }

    public static synchronized boolean isTagIdValid(int tagId) {
        for (Tag tag : allTags) {
            if (tag.getId() == tagId)
                return true;
        }
        return false;
    }

    public static synchronized boolean doesTagAlreadyExist(String text) {
        for (Tag tag : allTags) {
            if (tag.getTagString().equals(text))
                return true;
        }
        return false;
    }

    public static synchronized boolean doesTagAlreadyExist(int tagId) {
        for (Tag tag : allTags) {
            if (tag.getId() == tagId)
                return true;
        }
        return false;
    }

    public static synchronized boolean doesPlayerHaveTag(UUID playerUuid, Integer tagId) {
        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(playerUuid);

        for (Integer currentTagId : tagsOfPlayer) {
            if (tagId.equals(currentTagId)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized List<Integer> getTagsOfPlayerOrCreate(UUID playerUuid) {
        List<Integer> tagsOfPlayer = usedTags.get(playerUuid);

        if (tagsOfPlayer == null) {
            // Wenn der Spieler noch keine Tag Liste hat
            List<Integer> newPlayersTagList = new ArrayList<>();
            usedTags.put(playerUuid, newPlayersTagList);

            tagsOfPlayer = newPlayersTagList;
        }

        return tagsOfPlayer;
    }
}