package com.vunilly.vanillie.tag.menu;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    public TagMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.tag.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 3);

        ItemStack listTagsButton = Utils.getUiButton(new ItemStack(Material.WRITABLE_BOOK),
                Lang.get("menu.tag.server.txt").getFirst(), 1, Lang.get("menu.tag.server.desc"));
        inventory.setItem(9 + 2, listTagsButton);

        ItemStack newTagButton = Utils.getUiButton(new ItemStack(Material.NETHER_STAR),
                Lang.get("menu.tag.new.txt").getFirst(), 1, Lang.get("menu.tag.new.desc"));
        inventory.setItem(9 + 4, newTagButton);

        ItemStack myTagsButton = Utils.getUiButton(new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA),
                Lang.get("menu.tag.my.txt").getFirst(), 1, Lang.get("menu.tag.my.desc"));
        inventory.setItem(9 + 6, myTagsButton);
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 + 2):
                TagMenuServerlist tagMenuServerList = new TagMenuServerlist(player, 0, 0);
                player.openInventory(tagMenuServerList.getInventory());
                break;

            case (9 + 4):
                if (TagManager.getTagsByCreator(this.playerUUID).size() < TagManager.createLimit) {
                    TagMenuNew tagMenuNew = new TagMenuNew(player, new ArrayList<>(), "");
                    player.openInventory(tagMenuNew.getInventory());
                } else {
                    player.sendMessage(
                            Lang.get("msg.tag.createLimitReached", Placeholder.parsed("limit", String.valueOf(TagManager.createLimit)))
                                    .getFirst());
                }
                break;

            case (9 + 6):
                TagMenuMy tagMenuMy = new TagMenuMy(player, 0);
                player.openInventory(tagMenuMy.getInventory());
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