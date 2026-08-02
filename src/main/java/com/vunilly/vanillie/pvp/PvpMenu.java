package com.vunilly.vanillie.pvp;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

public class PvpMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    public PvpMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.pvp.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        boolean isPvpOff = PvpManager.isPvpOff(playerUUID);
        if (isPvpOff) {
            Utils.decorateInventory(Material.RED_STAINED_GLASS_PANE, this.inventory, 3);
            ItemStack pvpOnButton = Utils.getUiButton(new ItemStack(Material.IRON_SWORD),
                    Lang.get("menu.pvp.on.txt").getFirst(), 1, Lang.get("menu.pvp.on.desc"));
            inventory.setItem(9 + 4, pvpOnButton);
        } else {
            Utils.decorateInventory(Material.LIME_STAINED_GLASS_PANE, this.inventory, 3);
            ItemStack pvpOffButton = Utils.getUiButton(new ItemStack(Material.LILY_OF_THE_VALLEY),
                    Lang.get("menu.pvp.off.txt").getFirst(), 1, Lang.get("menu.pvp.off.desc"));
            inventory.setItem(9 + 4, pvpOffButton);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 + 4):
                if (PvpManager.isPvpOff(player.getUniqueId())) {
                    PvpManager.setPvpOn(player.getUniqueId());
                } else {
                    PvpManager.setPvpOff(player.getUniqueId());
                }
                PvpMenu menu = new PvpMenu(player);
                player.openInventory(menu.getInventory());
                break;

            default:
                break;
        }
    }
}