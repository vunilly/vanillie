package com.meinilly.vanillie.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import net.kyori.adventure.text.Component;

public class Utils {
    public static ItemStack getUiButton(ItemStack itemStack, Component title, int count, List<Component> description) {
        ItemMeta buttonMeta = itemStack.getItemMeta();

        buttonMeta.displayName(title);
        buttonMeta.lore(description);

        itemStack.setItemMeta(buttonMeta);
        itemStack.setAmount(count);

        return itemStack;
    }

    public static ItemStack createCustomHeadItem(String base64Texture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile("textures");
        profile.setProperty(new ProfileProperty("textures", base64Texture));

        skullMeta.setPlayerProfile(profile);

        head.setItemMeta(skullMeta);

        return head;
    }

    public static ItemStack createCustomHeadItem(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        // Direkt vom Online-Spieler
        skullMeta.setPlayerProfile(player.getPlayerProfile());

        head.setItemMeta(skullMeta);
        return head;
    }

    public static void decorateInventory(Material material, Inventory inventory, int rows) {
        ItemStack decorStack = new ItemStack(material);
        ItemStack decoration = getUiButton(decorStack, Component.text(""), 1, new ArrayList<>());

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, decoration);
            inventory.setItem(i + (rows - 1) * 9, decoration);
        }

        for (int j = 0; j < rows - 1; j++) {
            inventory.setItem(0 + j * (9), decoration);
            inventory.setItem(8 + j * (9), decoration);
        }
    }

    public static void decorateInventoryWithRainbow(Inventory inventory, int rows) {
        Material[] matList = {
                Material.RED_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE,
                Material.MAGENTA_STAINED_GLASS_PANE,
                Material.PINK_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE
        };

        ArrayList<ItemStack> decorations = new ArrayList<ItemStack>();

        for (Material mat : matList) {
            ItemStack decorStack = new ItemStack(mat);
            ItemStack decoration = getUiButton(decorStack, Component.text(""), 1, new ArrayList<>());

            decorations.add(decoration);
        }

        int counter = 0;
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, decorations.get(counter%decorations.size()));
            counter++;
            inventory.setItem(i + (rows - 1) * 9, decorations.get(counter%decorations.size()));
            counter++;
        }

        for (int j = 0; j < rows - 1; j++) {
            inventory.setItem(0 + j * (9), decorations.get(counter%decorations.size()));
            counter++;
            inventory.setItem(8 + j * (9), decorations.get(counter%decorations.size()));
            counter++;
        }
    }

    public static String getGradientText(String text) {
        return "<gradient:#7f6bff:#c4abff>" + text + "</gradient>";
    }

    public static String getMainColor() {
        return "7f6bff";
    }
}
