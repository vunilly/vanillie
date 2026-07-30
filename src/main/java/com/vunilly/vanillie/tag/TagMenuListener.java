package com.vunilly.vanillie.tag;

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
import org.bukkit.inventory.InventoryHolder;

import com.vunilly.vanillie.tag.menu.TagMenu;
import com.vunilly.vanillie.tag.menu.TagMenuColorBuilder;
import com.vunilly.vanillie.tag.menu.TagMenuNew;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.ColorList;

public class TagMenuListener implements Listener {
    private boolean isMenu(InventoryHolder holder) {
        return holder instanceof TagMenu || holder instanceof TagMenuColorBuilder || holder instanceof ColorList
                || holder instanceof ColorList || holder instanceof TagMenuNew;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        // 1. Prüfen, ob das geklickte Inventar unser Interface nutzt
        if (!(event.getInventory().getHolder() instanceof ClickMenu)) {
            return;
        }

        event.setCancelled(true);
        event.setResult(Result.DENY);

        switch (event.getClick()) {
            case LEFT:
                
            case RIGHT:
                break;

            default:
                return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
            return;
        }

        // 2. Das magische Stück: Wir casten den Holder zu unserem Interface
        ClickMenu menu = (ClickMenu) event.getInventory().getHolder();

        // 3. Wir rufen einfach die Methode auf. Da es nicht mehr static ist,
        // weiß das jeweilige Objekt GANZ VON ALLEINE, was es tun muss!
        menu.handleClick(event.getRawSlot(), event.getClick(), player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!isMenu(event.getInventory().getHolder())) {
            return;
        }

        event.setCancelled(true);
        event.setResult(Result.DENY);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!isMenu(event.getInventory().getHolder())) {
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!isMenu(event.getInventory().getHolder())) {
            return;
        }

        Player player = (Player) event.getPlayer();
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (!isMenu(event.getPlayer().getInventory().getHolder())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!isMenu(event.getPlayer().getInventory().getHolder())) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getPlayer();
        player.updateInventory();
    }
}
