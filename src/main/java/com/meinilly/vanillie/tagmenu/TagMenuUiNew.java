package com.meinilly.vanillie.tagmenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenuUiNew implements InventoryHolder {
    private Inventory inventory;

    public TagMenuUiNew() {
        this.inventory = Bukkit.createInventory(this, 9, Component.text("Test"));
        setupInventory();
    }

    private void setupInventory() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        inventory.setItem(4, item);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
