package com.meinilly.vanillie.tag.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.eclipse.sisu.launch.Main;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.Vanillie;
import com.meinilly.vanillie.tag.TagManager;
import com.meinilly.vanillie.utils.ClickMenu;
import com.meinilly.vanillie.utils.Lang;
import com.meinilly.vanillie.utils.SignUi;
import com.meinilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenuNew implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private final static MiniMessage minimessage = MiniMessage.miniMessage();
    public ArrayList<String> colorList = new ArrayList<>();
    public String tagText = "";

    public TagMenuNew(Player player, ArrayList<String> colorList, String text) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.tag.new.title").getFirst());
        this.playerUUID = player.getUniqueId();
        this.colorList = colorList;
        this.tagText = text;
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 3);

        ItemStack listTagsButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdmZjEzNzc3NTQ1NjNhYjQxYjhhMDMwNWRhYzAzZGU2M2UwMmU1YTM5YTY5NTZhZmQ2Y2NhYmYyOTVhOTZkOCJ9fX0"),
                Lang.get("menu.tag.new.color").getFirst(), 1, Lang.get("menu.tag.new.color.desc"));
        inventory.setItem(9 + 2, listTagsButton);

        ItemStack textButton = Utils.getUiButton(new ItemStack(Material.OAK_SIGN),
                Lang.get("menu.tag.new.text").getFirst(), 1, Lang.get("menu.tag.new.text.desc"));
        inventory.setItem(9 + 4, textButton);

        ItemStack confirmButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0"),
                Lang.get("menu.tag.new.save").getFirst(), 1, Lang.get("menu.tag.new.save.desc"));
        inventory.setItem(9 + 6, confirmButton);

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW),
                Lang.get("menu.backCancel").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 * 2 + 4, backButton);
    }

    @Override
    public void handleClick(int slotId, Player player) {
        switch (slotId) {
            case (9 + 2):
                TagMenuColorBuilder tagMenuColorSelector = new TagMenuColorBuilder(player, this.colorList, this.tagText);
                player.openInventory(tagMenuColorSelector.getInventory());
                break;

            case (9 + 4):
                openInput(player, this.colorList);
                break;

            case (9 + 6):
                TagManager.createTag(player.getUniqueId(), null);
                break;

            case (9 * 2 + 4):
                TagMenu tagMenu = new TagMenu(player);
                player.openInventory(tagMenu.getInventory());
                break;

            default:
                break;
        }
    }

    public static void openInput(Player player, ArrayList<String> colorHexCodeList) {
        player.closeInventory();
        
        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);
    
        // Öffne das SignUI
        plugin.getSignUI().open(player, new Component[] {
            Component.text(""),
            Component.text("^^^^^^^^^^"),
            Component.text("Text in die erste"),
            Component.text("Zeile eingeben")
        }, (inputLines) -> {
            String text = inputLines[0]; 
            if (text != null && !text.isBlank()) {
                player.sendMessage(Component.text("Tag gespeichert: " + text));
            }
            
            TagMenuNew t = new TagMenuNew(player, colorHexCodeList, text);
            player.openInventory(t.getInventory());
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
