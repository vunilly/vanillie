package com.vunilly.vanillie.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

public interface ClickMenu extends InventoryHolder {
    void handleClick(int slotId, ClickType clickType, Player player);
}
