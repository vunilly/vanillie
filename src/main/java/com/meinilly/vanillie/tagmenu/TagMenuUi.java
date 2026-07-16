package com.meinilly.vanillie.tagmenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.Vanillie;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenuUi implements InventoryHolder {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();
    private Inventory inventory;

    public TagMenuUi() {
        this.inventory = Bukkit.createInventory(this, 27, miniMessage.deserialize(Vanillie.getGradientText("Tag Menü")));
        setupInventory();
    }

    private void setupInventory() {
        // List Button
        ItemStack listButton = new ItemStack(Material.BOOK);
        ItemMeta listItemMeta = listButton.getItemMeta();
        listItemMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Zeige alle Tags auf dem Server")));

        List<Component> listLore = new ArrayList<>();
        listLore.add(miniMessage.deserialize(Vanillie.getGradientText("Hier kannst du Tags von anderen aktivieren.")));
        listLore.add(miniMessage.deserialize(Vanillie.getGradientText("Insgesammt existieren 14 Tags.")));
        listItemMeta.lore(listLore);

        listButton.setAmount(14);
        listButton.setItemMeta(listItemMeta);

        inventory.setItem(9+2, listButton);


        // New Button
        ItemStack newButton = new ItemStack(Material.NETHER_STAR);
        ItemMeta newItemMeta = newButton.getItemMeta();
        newItemMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Neuen Tag erstellen")));
        newButton.setItemMeta(newItemMeta);

        inventory.setItem(9+4, newButton);


        // Settings Button
        ItemStack settingsButton = new ItemStack(Material.COMPARATOR);
        ItemMeta settingsItemMeta = settingsButton.getItemMeta();
        settingsItemMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Verwalte bereits existierende Tags")));

        List<Component> settingsLore = new ArrayList<>();
        settingsLore.add(miniMessage.deserialize(Vanillie.getGradientText("Hier kannst deine Tags verwalten.")));
        settingsLore.add(miniMessage.deserialize(Vanillie.getGradientText("Du hast 2 Tags aktiviert.")));
        settingsLore.add(miniMessage.deserialize(Vanillie.getGradientText("Du hast 5 Tags erstellt.")));
        settingsItemMeta.lore(settingsLore);

        settingsButton.setAmount(2);
        settingsButton.setItemMeta(settingsItemMeta);

        inventory.setItem(9+6, settingsButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
    
}
