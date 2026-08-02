package com.vunilly.vanillie.tag.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.tag.Tag;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagMenuServerlist implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    public int pageIndex;
    public int maxPage;
    private static final int PAGE_WIDTH = 7;
    private static final int PAGE_HEIGHT = 2;
    private static final int ITEMS_PER_PAGE = PAGE_WIDTH * PAGE_HEIGHT;
    private static final int PAGE_START = 9 + 1; // erste Item-Position
    private int sortMode = 0;
    private List<Tag> tagList = new ArrayList<>();

    public TagMenuServerlist(Player player, int pageIndex, int sortMode) {
        this.pageIndex = pageIndex;
        this.sortMode = sortMode;
        this.playerUUID = player.getUniqueId();
        if (this.sortMode == 0) {
            this.tagList = TagManager.getAllTags();
        } else if (this.sortMode == 1) {
            this.tagList = TagManager.getTagsByCreator(this.playerUUID);
        }
        this.maxPage = Math.max(0, (tagList.size() - 1) / ITEMS_PER_PAGE);

        this.inventory = Bukkit.createInventory(this, 36,
                Lang.get("menu.tag.server.title", Placeholder.parsed("page", String.valueOf(this.pageIndex + 1)),
                        Placeholder.parsed("maxpage", String.valueOf(this.maxPage + 1))).getFirst());

        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.PURPLE_STAINED_GLASS_PANE, inventory, 4);

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW), Lang.get("menu.back").getFirst(), 1,
                new ArrayList<>());
        inventory.setItem(9 * 3 + 4, backButton);

        if (this.pageIndex > 0) {
            ItemStack lastPageButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNzg0NTFiZjI2Y2Y0OWZkNWY1NGNkOGYyYjM3Y2QyNWM5MmU1Y2E3NjI5OGIzNjM0Y2I1NDFlOWFkODkifX19"),
                    Lang.get("menu.page.last").getFirst(), pageIndex, new ArrayList<>());
            inventory.setItem(9 * 3 + 2, lastPageButton);
        }

        if (this.pageIndex < maxPage) {
            ItemStack nextPageButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE3ZjM2NjZkM2NlZGZhZTU3Nzc4Yzc4MjMwZDQ4MGM3MTlmZDVmNjVmZmEyYWQzMjU1Mzg1ZTQzM2I4NmUifX19"),
                    Lang.get("menu.page.next").getFirst(), pageIndex + 2, new ArrayList<>());
            inventory.setItem(9 * 3 + 6, nextPageButton);
        }

        if (this.sortMode == 0) {
            ItemStack allFilterButton = Utils.getUiButton(new ItemStack(Material.ENCHANTED_BOOK),
                    Lang.get("menu.tag.server.filter.txt").getFirst(), 1, Lang.get("menu.tag.server.filter.desc.all"));
            inventory.setItem(4, allFilterButton);
        } else if (this.sortMode == 1) {
            ItemStack myFilterButton = Utils.getUiButton(new ItemStack(Material.BOOK),
                    Lang.get("menu.tag.server.filter.txt").getFirst(), 1, Lang.get("menu.tag.server.filter.desc.mine"));
            inventory.setItem(4, myFilterButton);
        }

        // Farben anzeigen
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int tagCount = this.tagList.size();

        if (tagCount == 0) {
            ItemStack zeroTagsButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3NDU1ZGFlOTY5ODgyM2U0MDg3MjA5Y2M4ZTc2NDMxOWViYzMzNDhmN2ZhYmFhOWE4MTU1OTRmYzY0ZSJ9fX0"),
                    Lang.get("menu.tag.server.emptyPage.txt").getFirst(), 1,
                    Lang.get("menu.tag.server.emptyPage.desc"));
            inventory.setItem(9 + 4, zeroTagsButton);
        } else {
            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                int row = i % PAGE_WIDTH;
                int col = (i / PAGE_WIDTH) * 9;

                int inventorySlot = PAGE_START + row + col;
                int tagIndex = startIndex + i;

                if (tagIndex >= tagCount) {
                    break;
                }

                Tag tag = tagList.get(tagIndex);

                if (tag == null) {
                    ItemStack tagButton = Utils.getUiButton(new ItemStack(Material.PAPER),
                            Lang.get("menu.tag.tagFail.txt")
                                    .getFirst(),
                            1, new ArrayList<>());

                    inventory.setItem(inventorySlot, tagButton);

                    break;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
                String username = offlinePlayer.getName() != null ? offlinePlayer.getName() : Lang.getString("str.playerNameFail");
                
                int usercount = TagManager.getUsercountById(tag.getId());
                String tagText = tag.getTagString();

                List<Component> tagDescription = new ArrayList<>();
                if (TagManager.doesPlayerHaveTag(this.playerUUID, tag.getId())) {
                    tagDescription = Lang.get("menu.tag.server.tag.desc.active",
                            Placeholder.parsed("ownername", username),
                            Placeholder.parsed("usercount", String.valueOf(usercount)));
                } else {
                    tagDescription = Lang.get("menu.tag.server.tag.desc.inactive",
                            Placeholder.parsed("ownername", username),
                            Placeholder.parsed("usercount", String.valueOf(usercount)));
                }
                if (usercount <= 0) {
                    usercount = 1;
                }
                ItemStack tagButton = Utils.getUiButton(Utils.getPlayerHead(username),
                        Lang.get("menu.tag.server.tag.txt",
                                Placeholder.parsed("tag", tagText),
                                Placeholder.parsed("id", String.valueOf(tagIndex)))
                                .getFirst(),
                        usercount, tagDescription);

                inventory.setItem(inventorySlot, tagButton);
            }
        }
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (4):
                TagMenuServerlist tagMenuServerList = new TagMenuServerlist(player, 0, (this.sortMode + 1) % 2);
                player.openInventory(tagMenuServerList.getInventory());
                break;

            case (9 * 3 + 4):
                TagMenu tagMenu = new TagMenu(player);
                player.openInventory(tagMenu.getInventory());
                break;

            // last page
            case (9 * 3 + 2):
                if (pageIndex <= 0) {
                    break;
                }
                TagMenuServerlist tagMenuServerlistLastPage = new TagMenuServerlist(player, pageIndex - 1,
                        this.sortMode);
                player.openInventory(tagMenuServerlistLastPage.getInventory());
                break;

            // next page
            case (9 * 3 + 6):
                if (pageIndex >= maxPage) {
                    break;
                }
                TagMenuServerlist tagMenuServerlistNextPage = new TagMenuServerlist(player, pageIndex + 1,
                        this.sortMode);
                player.openInventory(tagMenuServerlistNextPage.getInventory());
                break;

            default:
                handleColorClick(slotId, clickType, player);
                break;
        }
    }

    private void handleColorClick(int slotId, ClickType clickType, Player player) {
        if (slotId < PAGE_START || slotId > PAGE_START + (9 * PAGE_HEIGHT) - 1 || slotId % 9 == 0 || slotId % 9 == 8)
            return;

        int row = (slotId - PAGE_START) / 9;
        int col = (slotId - PAGE_START) % 9;

        int index = row * PAGE_WIDTH + col + ITEMS_PER_PAGE * pageIndex;

        if (index > this.tagList.size() - 1)
            return;

        Tag tag = this.tagList.get(index);
        if (clickType == ClickType.LEFT) {
            Component result;
            
            if (TagManager.doesPlayerHaveTag(this.playerUUID, tag.getId())) {
                result = TagManager.deactivateTag(this.playerUUID, tag.getId());
            } else {

                if (TagManager.getAllActiveTagsForPlayer(this.playerUUID).size() < TagManager.activeLimit) {
                    result = TagManager.activateTag(this.playerUUID, tag.getId());
                } else {
                    result = Lang.get("msg.tag.activeLimitReached",
                            Placeholder.parsed("limit", String.valueOf(TagManager.activeLimit)))
                            .getFirst();
                }
            }

            TagMenuServerlist tagMenuServerlist = new TagMenuServerlist(player, this.pageIndex, this.sortMode);
            player.sendMessage(result);
            player.openInventory(tagMenuServerlist.getInventory());
        } else if (clickType == ClickType.RIGHT) {
            TagMenuDelete tagMenuDelete = new TagMenuDelete(player, tag.getId());
            player.openInventory(tagMenuDelete.getInventory());
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}