package com.vunilly.vanillie.pvp;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class PvpManager {
    private static final Set<UUID> pvpOffPlayers = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Integer> pvpTimers = new ConcurrentHashMap<>();
    private static final int COMBAT_TIME = 60;
    private static File dataFile;

    // Private Constructor (Utility-Class Pattern)
    private PvpManager() {
    }

    public static void init(File pluginFolder) {
        dataFile = new File(pluginFolder, "pvp_config.yml");
    }

    public static void clearData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        config.set("pvp.players", null);
        config.set("players", null);

        pvpOffPlayers.clear();
        pvpTimers.clear();
    }

    public static void loadData() {
        if (dataFile == null || !dataFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        if (config.contains("pvp.players")) {
            List<String> uuidList = config.getStringList("pvp.players");
            for (String uuidStr : uuidList) {
                try {
                    pvpOffPlayers.add(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("[Vanillie] Invalid UUID in pvp config: " + uuidStr);
                }
            }
        }
    }

    public static void saveData() {
        if (dataFile == null) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        List<String> uuidList = pvpOffPlayers.stream()
                .map(UUID::toString)
                .toList();
        config.set("pvp.players", uuidList);

        try {
            config.save(dataFile);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Failed to save PVP config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isPvpOff(UUID uuid) {
        return pvpOffPlayers.contains(uuid);
    }

    public static Set<UUID> getPvpOffPlayers() {
        return new HashSet<>(pvpOffPlayers);
    }

    public static boolean isInCombat(UUID uuid) {
        return pvpTimers.containsKey(uuid);
    }

    public static int getPlayerTimer(UUID uuid) {
        return pvpTimers.getOrDefault(uuid, 0);
    }

    public static void startCombat(UUID uuid) {
        pvpTimers.put(uuid, COMBAT_TIME);
    }

    public static void setPvpOn(UUID uuid) {
        pvpOffPlayers.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(Lang.get("msg.pvp.on").getFirst());
        }
    }

    public static boolean setPvpOff(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return false; // Oder anders handhaben
        }
        if (isInCombat(uuid)) {
            player.sendMessage(Lang.get("msg.pvp.cooldown",
                    Placeholder.parsed("secs", String.valueOf(getPlayerTimer(uuid))))
                    .getFirst());
            return false;
        }
        AttributeInstance scale = player.getAttribute(Attribute.SCALE);
        if (scale.getBaseValue() <= 0.8) {
            player.sendMessage(Lang.get("msg.pvp.tooLow.you").getFirst());
            return false;
        }
        pvpOffPlayers.add(uuid);
        player.sendMessage(Lang.get("msg.pvp.off").getFirst());
        return true;
    }

    public static void startTimerTask() {
        Vanillie plugin = (Vanillie) Bukkit.getPluginManager().getPlugin("Vanillie");
        if (plugin == null) {
            Bukkit.getLogger().severe("[Vanillie] Could not start PVP timer task!");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                var iterator = pvpTimers.entrySet().iterator();

                while (iterator.hasNext()) {
                    var entry = iterator.next();
                    UUID uuid = entry.getKey();
                    int time = entry.getValue();
                    Player player = Bukkit.getPlayer(uuid);

                    if (time <= 1) {
                        iterator.remove();
                        if (player != null) {
                            player.sendActionBar(Lang.get("msg.pvp.combatEnded").getFirst());
                        }
                        continue;
                    }

                    entry.setValue(time - 1);
                    if (player != null) {
                        player.sendActionBar(
                                Lang.get("msg.pvp.combatSecs",
                                        Placeholder.parsed("secs", String.valueOf(time - 1)))
                                        .getFirst());
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}