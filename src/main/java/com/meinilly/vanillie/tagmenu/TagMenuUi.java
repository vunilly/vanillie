package com.meinilly.vanillie.tagmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.Vanillie;
import com.meinilly.vanillie.commands.tag.TagManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenuUi implements InventoryHolder {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();
    private UUID playerUUID;
    private Inventory inventory;

    public TagMenuUi(Player player) {
        this.inventory = Bukkit.createInventory(this, 27,
                miniMessage.deserialize(Vanillie.getGradientText("Tag Menü")));
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    private void setupInventory() {
        ItemStack backgroundItem = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(Component.text(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, backgroundItem); // Obere Reihe
            inventory.setItem(2 * 9 + i, backgroundItem); // Untere Reihe
        }
        for (int i = 0; i < 3; i++) {
            inventory.setItem(i * 9, backgroundItem); // Linke Spalte
            inventory.setItem(i * 9 + 8, backgroundItem); // Rechte Spalte
        }

        int serverTagCount = TagManager.getServerTagList().size();
        int playerTagCreatedCount = TagManager.getPlayerCreatedTagCount(this.playerUUID);
        int playerTagUsedCount = TagManager.getPlayerUsedTags().get(this.playerUUID).size();

        // List Button
        ItemStack listButton = new ItemStack(Material.FEATHER);
        ItemMeta listItemMeta = listButton.getItemMeta();
        listItemMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Zeige alle Tags auf dem Server")));

        List<Component> listLore = new ArrayList<>();
        listLore.add(miniMessage.deserialize(Vanillie.getGradientText("Hier kannst du Tags von anderen aktivieren.")));
        listLore.add(miniMessage.deserialize(Vanillie.getGradientText("Insgesammt existieren " + serverTagCount +" Tags.")));
        listItemMeta.lore(listLore);
        
        listButton.setAmount(serverTagCount);
        listButton.setItemMeta(listItemMeta);

        inventory.setItem(9 + 2, listButton);

        // New Button
        ItemStack newButton = new ItemStack(Material.NETHER_STAR);
        ItemMeta newItemMeta = newButton.getItemMeta();
        newItemMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Neuen Tag erstellen")));
        newButton.setItemMeta(newItemMeta);

        inventory.setItem(9 + 4, newButton);

        // Settings Button
        ItemStack settingsButton = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta settingsItemMeta = settingsButton.getItemMeta();
        settingsItemMeta
                .displayName(miniMessage.deserialize(Vanillie.getGradientText("Verwalte bereits existierende Tags")));

        List<Component> settingsLore = new ArrayList<>();
        settingsLore.add(miniMessage.deserialize(Vanillie.getGradientText("Hier kannst deine Tags verwalten.")));
        settingsLore.add(miniMessage.deserialize(Vanillie.getGradientText("Du hast " + playerTagUsedCount + " Tags aktiviert.")));
        settingsLore.add(miniMessage.deserialize(Vanillie.getGradientText("Du hast " + playerTagCreatedCount +" Tags erstellt.")));
        settingsItemMeta.lore(settingsLore);

        settingsButton.setAmount(playerTagUsedCount);
        settingsButton.setItemMeta(settingsItemMeta);

        inventory.setItem(9 + 6, settingsButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}
