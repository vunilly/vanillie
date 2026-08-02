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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagMenuDelete implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private int tagIdToDelete;

    public TagMenuDelete(Player player, int tagId) {
        this.inventory = Bukkit.createInventory(this, 27, Lang
                .get("menu.tag.delete.title", Placeholder.parsed("tag",
                        TagManager.findTagById(tagId).getTagString()))
                .getFirst());
        this.playerUUID = player.getUniqueId();
        this.tagIdToDelete = tagId;
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.RED_STAINED_GLASS_PANE, inventory, 3);

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW), Lang.get("menu.back").getFirst(), 1,
                new ArrayList<>());
        inventory.setItem(9 * 2 + 4, backButton);

        ItemStack deleteButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3NDU1ZGFlOTY5ODgyM2U0MDg3MjA5Y2M4ZTc2NDMxOWViYzMzNDhmN2ZhYmFhOWE4MTU1OTRmYzY0ZSJ9fX0"),
                Lang.get("menu.tag.delete.txt",
                        Placeholder.parsed("tag", TagManager.findTagById(this.tagIdToDelete).getTagString()))
                        .getFirst(),
                1, Lang.get("menu.tag.delete.desc"));
        inventory.setItem(9 + 4, deleteButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 * 2 + 4):
                TagMenu tagMenu = new TagMenu(player);
                player.openInventory(tagMenu.getInventory());
                break;

            case (9 + 4):
                Component result = TagManager.deleteTag(playerUUID, this.tagIdToDelete, player.isOp());
                player.sendMessage(result);
                player.closeInventory();
                break;

            default:
                break;
        }
    }

}
