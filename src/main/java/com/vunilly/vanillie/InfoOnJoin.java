package com.vunilly.vanillie;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class InfoOnJoin implements Listener {

    private static final int INFO_BOOK_VERSION = 2;

    private final NamespacedKey infoBookKey;

    public InfoOnJoin() {
        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);
        this.infoBookKey = new NamespacedKey(plugin, "info_book_version");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);

        Player player = event.getPlayer();

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        int seenVersion = pdc.getOrDefault(
                infoBookKey,
                PersistentDataType.INTEGER,
                0
        );

        if (seenVersion >= INFO_BOOK_VERSION) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            InfoOnJoin.openInfoBook(player);

            pdc.set(
                    infoBookKey,
                    PersistentDataType.INTEGER,
                    INFO_BOOK_VERSION
            );
        }, 20L);
    }

    private static void openInfoBook(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setTitle("Vanillie Info");
        meta.setAuthor("Server");

        meta.addPages(
                Vanillie.minimessage.deserialize("""
                        <c:#ff0000>SMP UPDATE!</c>

                        <b>ALTES:</b>
                        /tag - Erstelle bunte Prefixe (Texte vor deinem Namen)
                        /vote - Wetter oder Tagszeit abstimmen
                        /pvp - PVP an/aus
                        <b>NEUES:</b>
                        /size - Spielergröße anpassen (pvp fair)
                        """));

        book.setItemMeta(meta);
        player.openBook(book);
    }
}