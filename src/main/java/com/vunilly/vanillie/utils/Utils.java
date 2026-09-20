package com.vunilly.vanillie.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.vunilly.vanillie.Vanillie;

import net.kyori.adventure.text.Component;

public class Utils {
    public static ItemStack getUiButton(ItemStack itemStack, Component title, int count, List<Component> description) {
        return getUiButton(itemStack, title, count, description, false);
    }

    public static ItemStack getUiButton(ItemStack itemStack, Component title, int count, List<Component> description, boolean enchanted) {
        ItemMeta buttonMeta = itemStack.getItemMeta();

        buttonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        buttonMeta.setAttributeModifiers(null);
        buttonMeta.displayName(title);
        buttonMeta.lore(null);
        buttonMeta.lore(description);
        buttonMeta.setEnchantmentGlintOverride(enchanted);

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

    public static ItemStack getPlayerHead(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.lore(null);
        head.setItemMeta(meta);

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
            inventory.setItem(i, decorations.get(counter % decorations.size()));
            counter++;
            inventory.setItem(i + (rows - 1) * 9, decorations.get(counter % decorations.size()));
            counter++;
        }

        for (int j = 0; j < rows - 1; j++) {
            inventory.setItem(0 + j * (9), decorations.get(counter % decorations.size()));
            counter++;
            inventory.setItem(8 + j * (9), decorations.get(counter % decorations.size()));
            counter++;
        }
    }

    public static Location getHandLocation(Player player, boolean isMainHand) {
        Location loc = player.getEyeLocation();
        
        boolean isRightHand = isMainHand 
                ? (player.getMainHand() == MainHand.RIGHT) 
                : (player.getMainHand() == MainHand.LEFT);

        Vector direction = loc.getDirection().normalize();

        Vector right = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        
        if (!isRightHand) {
            right.multiply(-1);
        }

        double rightOffset = 0.3; // Distance to the side
        double forwardOffset = 0.6; // Distance in front of the eyes
        double downOffset = 0.55;    // Distance below the eyes

        loc.add(right.multiply(rightOffset));
        loc.add(direction.multiply(forwardOffset));
        loc.subtract(0, downOffset, 0);

        return loc;
    }
}
