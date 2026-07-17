package com.meinilly.vanillie.tagmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.Vanillie;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenuUiNew implements InventoryHolder {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();
    private UUID playerUUID;
    private Inventory inventory;

    public TagMenuUiNew(Player player) {
        this.playerUUID = player.getUniqueId();
        this.inventory = Bukkit.createInventory(this, 27,
                miniMessage.deserialize(Vanillie.getGradientText("Neuen Tag erstellen...")));
        setupInventory();
    }

    private void setupInventory() {
        ItemStack backgroundItem = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(Component.text(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, backgroundItem);
            inventory.setItem(2*9 + i, backgroundItem);
        }
        for (int i = 0; i < 2; i++) {
            inventory.setItem(i*9, backgroundItem);
            inventory.setItem(i*9 + 8, backgroundItem);
        }

        // Color Selection
        ItemStack colorButton = Vanillie.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdmZjEzNzc3NTQ1NjNhYjQxYjhhMDMwNWRhYzAzZGU2M2UwMmU1YTM5YTY5NTZhZmQ2Y2NhYmYyOTVhOTZkOCJ9fX0");
        ItemMeta colorMeta = colorButton.getItemMeta();
        colorMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Wähle die Farbe des Tags")));

        List<Component> colorLore = new ArrayList<>();
        colorLore.add(miniMessage.deserialize(Vanillie.getGradientText("Du kannst eine Einzelne Farbe oder eine Verlauf auswählen")));
        colorMeta.lore(colorLore);
        colorButton.setItemMeta(colorMeta);

        inventory.setItem(9 + 2, colorButton);

        // Text Selection
        ItemStack textButton = Vanillie.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZjMzJjOTE0Mjc2ZjY4NjE0MTc5NmE1YTAyM2MzOWVmZGZlZDE0ZDNkN2M5YzQyNzkyNTEzODQ2ZjdmYTRiMyJ9fX0");
        ItemMeta textMeta = textButton.getItemMeta();
        textMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Wähle den Text des Tags")));
        textButton.setItemMeta(textMeta);

        inventory.setItem(9 + 3, textButton);

        // Preview Selection
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        ItemStack previewButton = Vanillie.createCustomHeadItem(offlinePlayer);
        ItemMeta previewMeta = previewButton.getItemMeta();
        previewMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("So wird dein Tag aussehen: " + "<color:red>TEST</color>")));
        previewButton.setItemMeta(previewMeta);

        inventory.setItem(9 + 4, previewButton);

        // Create Selection
        ItemStack createButton = Vanillie.createCustomHeadItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5ODBjMWQyMTE4MDlhOWI2NTY1MDg4ZjU2YTM4ZjJlZjQ5MTE1YzEwNTRmYTY2MjQ1MTIyZTllZWVkZWNjMiJ9fX0");
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Erstelle den Tag so und aktiviere ihn bei dir")));
        createButton.setItemMeta(createMeta);

        inventory.setItem(9 + 6, createButton);

        // Back Button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Zurück")));

        backButton.setItemMeta(backMeta);

        inventory.setItem(9*2 + 4, backButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
