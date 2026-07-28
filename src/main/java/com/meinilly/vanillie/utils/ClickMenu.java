package com.meinilly.vanillie.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public interface ClickMenu extends InventoryHolder {
    void handleClick(int slotId, Player player);
}
