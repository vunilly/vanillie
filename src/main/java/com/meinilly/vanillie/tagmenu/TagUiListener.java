package com.meinilly.vanillie.tagmenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class TagUiListener implements Listener {

    private boolean isTagUI(InventoryClickEvent event) {
        return event.getInventory().getHolder() instanceof TagMenuUi ||
                event.getInventory().getHolder() instanceof TagMenuUiList;
    }

    private boolean isTagUI(InventoryDragEvent event) {
        return event.getInventory().getHolder() instanceof TagMenuUi ||
                event.getInventory().getHolder() instanceof TagMenuUiList;
    }

    private boolean isTagUI(InventoryOpenEvent event) {
        return event.getInventory().getHolder() instanceof TagMenuUi ||
                event.getInventory().getHolder() instanceof TagMenuUiList;
    }

    private boolean isTagUI(InventoryCloseEvent event) {
        return event.getInventory().getHolder() instanceof TagMenuUi ||
                event.getInventory().getHolder() instanceof TagMenuUiList;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isTagUI(event)) {
            return;
        }

        // Block EVERYTHING by default
        event.setCancelled(true);
        event.setResult(Result.DENY);

        // Block all click types that can move items
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
            case NUMBER_KEY:
            case DOUBLE_CLICK:
            case DROP:
            case CONTROL_DROP:
            case SWAP_OFFHAND:
            case CREATIVE:
            case UNKNOWN:
                return;
            default:
                break;
        }

        // Ignore clicks in the player's inventory
        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        player.updateInventory();

        switch (event.getRawSlot()) {
            case 11:
                player.openInventory(new TagMenuUiList(player).getInventory());
                break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!isTagUI(event)) {
            return;
        }

        event.setCancelled(true);
        event.setResult(Result.DENY);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!isTagUI(event))
            return;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!isTagUI(event))
            return;

        // ← WICHTIG: Auch beim Schließen clearen!
        Player player = (Player) event.getPlayer();
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof TagMenuUi
                || event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof TagMenuUiList) {
            event.setCancelled(true);
            Player player = (Player) event.getPlayer();
            player.updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof TagMenuUi
                || event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof TagMenuUiList) {
            event.setCancelled(true);
            Player player = (Player) event.getPlayer();
            player.updateInventory();
        }
    }
}