package com.meinilly.vanillie.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import io.papermc.paper.math.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SignUi implements Listener {

    private final Map<UUID, SignSession> activeSessions = new HashMap<>();
    private final Plugin plugin;

    public SignUi(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, Component[] lines, Consumer<String[]> onFinish) {
        Position virtualPos = player.getLocation();

        Sign virtualSign = createVirtualSign(lines);

        Location loc = new Location(player.getWorld(), 
            virtualPos.blockX(), 
            virtualPos.blockY(), 
            virtualPos.blockZ());
        
        player.sendBlockChange(loc, Material.OAK_SIGN.createBlockData());
        player.sendBlockUpdate(loc, virtualSign);

        // Die Location speichern wir jetzt in der Session ab
        activeSessions.put(player.getUniqueId(), new SignSession(virtualSign, loc, onFinish));
        try {
            player.openVirtualSign(virtualPos, Side.FRONT);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Fehler beim Öffnen von SignUi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Sign createVirtualSign(Component[] lines) {
        BlockState state = Bukkit.createBlockData(Material.OAK_SIGN).createBlockState();
        Sign sign = (Sign) state;

        if (lines != null) {
            for (int i = 0; i < lines.length && i < 4; i++) {
                if (lines[i] != null) {
                    sign.getSide(Side.FRONT).line(i, lines[i]);
                }
            }
        }

        return sign;
    }

    @EventHandler
    public void onUncheckedSignChange(UncheckedSignChangeEvent event) {
        Player player = event.getPlayer();
        
        if (player == null) {
            Bukkit.getLogger().warning("[Vanillie] Player ist null in UncheckedSignChangeEvent in SignUi!");
            return;
        }
        
        SignSession session = activeSessions.remove(player.getUniqueId());

        if (session == null) {
            return;
        }

        // Schild verstecken: Original-Block aus der Welt an den Spieler senden
        player.sendBlockChange(session.location, session.location.getBlock().getBlockData());

        String[] input = extractSignInput(event);

        try {
            session.onFinish.accept(input);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Error on callback in SignUi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] extractSignInput(UncheckedSignChangeEvent event) {
        String[] input = new String[4];
        try {
            var lines = event.lines();
            for (int i = 0; i < 4; i++) {
                Component line = lines.get(i);
                if (line != null) {
                    input[i] = PlainTextComponentSerializer.plainText().serialize(line);
                } else {
                    input[i] = "";
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Vanillie] Error extracting text in SignUi: " + e.getMessage());
            e.printStackTrace();
        }
        return input;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        activeSessions.remove(event.getPlayer().getUniqueId());
    }

    private static class SignSession {
        final Sign virtualSign;
        final Location location;
        final Consumer<String[]> onFinish;

        SignSession(Sign virtualSign, Location location, Consumer<String[]> onFinish) {
            this.virtualSign = virtualSign;
            this.location = location;
            this.onFinish = onFinish;
        }
    }
}