package com.vunilly.vanillie.tag;

import java.io.File;
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
import org.bukkit.entity.Player;

import com.vunilly.vanillie.tag.display.TagNametagListener;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagManager {
    private final static MiniMessage minimessage = MiniMessage.miniMessage();
    private static final List<Tag> allTags = Collections.synchronizedList(new ArrayList<>());
    private static final Map<UUID, List<Integer>> usedTags = new ConcurrentHashMap<>();
    private static File dataFile;
    public static int createLimit = 5;
    public static int activeLimit = 3;

    public static void clearData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        config.set("tags", null);
        config.set("players", null);

        allTags.clear();
        usedTags.clear();
    }

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
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save tag config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void init(File pluginFolder) {
        dataFile = new File(pluginFolder, "tag_config.yml");
    }

    public static synchronized List<Integer> getAllActiveTagsForPlayer(UUID playerUuid) {
        List<Integer> playersTags = usedTags.get(playerUuid);
        if (playersTags == null) {
            playersTags = new ArrayList<>();
        }
        return playersTags;
    }

    public static synchronized List<Tag> getAllTags() {
        return allTags;
    }

    public static synchronized List<Tag> getTagsByCreator(UUID playerUuid) {
        List<Tag> tagList = new ArrayList<>();
        for (Tag tag : allTags) {
            if (tag.getOwnerUUID().equals(playerUuid)) {
                tagList.add(tag);
            }
        }
        return tagList;
    }

    public static synchronized Map<UUID, List<Integer>> getUsedTags() {
        return usedTags;
    }

    private static synchronized int findNewId() {
        int id = 0;

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

        if (TagManager.getAllActiveTagsForPlayer(ownerUuid).size() >= TagManager.activeLimit) {
            allTags.add(newTag);
            return Lang.get("msg.tag.createdButNotActivated", Placeholder.parsed("tag", newTag.getTagString()), Placeholder.parsed("limit", String.valueOf(TagManager.activeLimit))).getFirst();
        } else {
            allTags.add(newTag);
            activateTag(ownerUuid, newId);
            return Lang.get("msg.tag.created", Placeholder.parsed("tag", newTag.getTagString())).getFirst();
        }
    }

    public static synchronized Component deleteTag(UUID ownerUuid, int tagId, boolean isOp) {
        if (!doesTagAlreadyExist(tagId))
            return Lang.get("msg.tag.invalidId", Placeholder.parsed("id", String.valueOf(tagId))).getFirst();

        // sollte nie null sein, weil wir es oben gecheckt haben
        Tag toDeleteTag = findTagById(tagId);
        if (toDeleteTag == null) {
            throw new IllegalStateException("Tag exists in allTags list, but somehow doesnt! This cant happen.");
        }

        if ((!toDeleteTag.getOwnerUUID().equals(ownerUuid)) && !isOp)
            return Lang.get("msg.tag.notYours").getFirst();

        // deactivate from all
        for (UUID playerUuid : usedTags.keySet()) {
            if (doesPlayerHaveTag(playerUuid, tagId)) {
                deactivateTag(playerUuid, tagId);
            }
        }

        allTags.remove(toDeleteTag);

        return Lang.get("msg.tag.deleted", Placeholder.parsed("tag", toDeleteTag.getTagString())).getFirst();
    }

    public static synchronized Component activateTag(UUID addToWho, int tagId) {
        if (doesPlayerHaveTag(addToWho, tagId))
            return Lang.get("msg.tag.alreadyActivated").getFirst();
        if (!isTagIdValid(tagId))
            return Lang.get("msg.tag.invalidId", Placeholder.parsed("id", String.valueOf(tagId))).getFirst();

        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(addToWho);

        tagsOfPlayer.add(tagId);
        
        Player player = Bukkit.getPlayer(addToWho);
        TagNametagListener.updateNametag(player);

        return Lang.get("msg.tag.activated", Placeholder.parsed("tag", 
        findTagById(tagId).getTagString())).getFirst();
    }

    public static synchronized Component deactivateTag(UUID removeFromWho, int tagId) {
        if (!doesPlayerHaveTag(removeFromWho, tagId))
            return Lang.get("msg.tag.alreadyDeactivated").getFirst();
        if (!isTagIdValid(tagId))
            return Lang.get("msg.tag.invalidId", Placeholder.parsed("id", String.valueOf(tagId))).getFirst();

        List<Integer> tagsOfPlayer = getTagsOfPlayerOrCreate(removeFromWho);

        tagsOfPlayer.remove(Integer.valueOf(tagId));

        Player player = Bukkit.getPlayer(removeFromWho);
        TagNametagListener.updateNametag(player);

        return Lang.get("msg.tag.deactivated", Placeholder.parsed("tag", 
        findTagById(tagId).getTagString())).getFirst();
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

    public static synchronized Tag findTagById(int tagId) {
        for (Tag tag : allTags) {
            if (tag.getId() == tagId) {
                return tag;
            }
        }
        return null;
    }

    public static synchronized int getUsercountById(int tagId) {
        int count = 0;
        for (List<Integer> playerUsedTags : usedTags.values()) {
            if (playerUsedTags.contains(tagId)) {
                count++;
            }
        }
        return count;
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

    public static synchronized Component getPlayerTags(UUID playerUuid) {
        List<Integer> playerTagIds = getAllActiveTagsForPlayer(playerUuid);

        if (playerTagIds == null || playerTagIds.isEmpty()) {
            return Component.empty();
        }

        List<Component> tagComponents = new ArrayList<>();
        for (Integer tagId : playerTagIds) {
            Tag tag = findTagById(tagId);
            if (tag != null) {
                tagComponents.add(minimessage.deserialize(tag.getTagString()));
            }
        }

        return Component.join(
                JoinConfiguration.separator(Component.space()),
                tagComponents);
    }
}