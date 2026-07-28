package com.meinilly.vanillie.tag.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.utils.ClickMenu;
import com.meinilly.vanillie.utils.Lang;
import com.meinilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private final static MiniMessage minimessage = MiniMessage.miniMessage();

    public TagMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.tag.tile").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 3);

        ItemStack listTagsButton = Utils.getUiButton(new ItemStack(Material.WRITABLE_BOOK),
                Lang.get("menu.tag.server").getFirst(), 1, Lang.get("menu.tag.server.desc"));
        inventory.setItem(9 + 2, listTagsButton);

        ItemStack newTagButton = Utils.getUiButton(new ItemStack(Material.NETHER_STAR),
                Lang.get("menu.tag.new").getFirst(), 1, Lang.get("menu.tag.new.desc"));
        inventory.setItem(9 + 4, newTagButton);

        ItemStack myTagsButton = Utils.getUiButton(new ItemStack(Material.COMPARATOR),
                Lang.get("menu.tag.my").getFirst(), 1, Lang.get("menu.tag.my.desc"));
        inventory.setItem(9 + 6, myTagsButton);
    }

    @Override
    public void handleClick(int slotId, Player player) {
        switch (slotId) {
            case (9 + 4):
                TagMenuNew tagMenuNew = new TagMenuNew(player, new ArrayList<>(), "");
                player.openInventory(tagMenuNew.getInventory());
                break;
            default:
                break;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}