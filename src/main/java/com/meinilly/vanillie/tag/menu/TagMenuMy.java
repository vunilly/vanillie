package com.meinilly.vanillie.tag.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.tag.Tag;
import com.meinilly.vanillie.tag.TagManager;
import com.meinilly.vanillie.utils.ClickMenu;
import com.meinilly.vanillie.utils.ColorList;
import com.meinilly.vanillie.utils.Colors;
import com.meinilly.vanillie.utils.Lang;
import com.meinilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagMenuMy implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private final static MiniMessage minimessage = MiniMessage.miniMessage();
    public int pageIndex;
    public int maxPage;
    private static final int PAGE_WIDTH = 7;
    private static final int PAGE_HEIGHT = 4;
    private static final int ITEMS_PER_PAGE = PAGE_WIDTH * PAGE_HEIGHT;
    private static final int PAGE_START = 9 + 1; // erste Item-Position

    public TagMenuMy(Player player, int pageIndex) {
        this.playerUUID = player.getUniqueId();

        this.pageIndex = pageIndex;
        this.maxPage = (int) Math.ceil((double) TagManager.getAllActiveTagsForPlayer(this.playerUUID).size() / ITEMS_PER_PAGE)
                - 1;

        this.inventory = Bukkit.createInventory(this, 54,
                Lang.get("menu.tag.my.title", Placeholder.parsed("page", String.valueOf(this.pageIndex+1)),
                        Placeholder.parsed("maxpage", String.valueOf(this.maxPage+1))).getFirst());

        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 6);

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW), Lang.get("menu.back").getFirst(), 1,
                new ArrayList<>());
        inventory.setItem(9 * 5 + 4, backButton);

        if (this.pageIndex > 0) {
            ItemStack lastPageButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNzg0NTFiZjI2Y2Y0OWZkNWY1NGNkOGYyYjM3Y2QyNWM5MmU1Y2E3NjI5OGIzNjM0Y2I1NDFlOWFkODkifX19"),
                    Lang.get("menu.page.last").getFirst(), pageIndex, new ArrayList<>());
            inventory.setItem(9 * 5 + 2, lastPageButton);
        }

        if (this.pageIndex < maxPage) {
            ItemStack nextPageButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE3ZjM2NjZkM2NlZGZhZTU3Nzc4Yzc4MjMwZDQ4MGM3MTlmZDVmNjVmZmEyYWQzMjU1Mzg1ZTQzM2I4NmUifX19"),
                    Lang.get("menu.page.next").getFirst(), pageIndex + 2, new ArrayList<>());
            inventory.setItem(9 * 5 + 6, nextPageButton);
        }

        // Farben anzeigen
        int startIndex = pageIndex * ITEMS_PER_PAGE;

        List<Integer> allPlayerTags = TagManager.getAllActiveTagsForPlayer(this.playerUUID);
        int tagCount = allPlayerTags.size();

        if (tagCount == 0) {
            ItemStack zeroTagsButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3NDU1ZGFlOTY5ODgyM2U0MDg3MjA5Y2M4ZTc2NDMxOWViYzMzNDhmN2ZhYmFhOWE4MTU1OTRmYzY0ZSJ9fX0"),
                    Lang.get("menu.tag.my.emptyPage.txt").getFirst(), 1, Lang.get("menu.tag.my.emptyPage.desc"));
            inventory.setItem(9 * 3 + 4, zeroTagsButton);
        } else {
            for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                int row = i % PAGE_WIDTH;
                int col = (i / PAGE_WIDTH) * 9;

                int inventorySlot = PAGE_START + row + col;
                int tagIndex = startIndex + i;

                if (tagIndex >= tagCount) {
                    break;
                }

                Integer tagId = allPlayerTags.get(tagIndex);
                Tag tag = TagManager.findTagById(tagId);

                if (tag == null) {
                    ItemStack tagButton = Utils.getUiButton(new ItemStack(Material.PAPER),
                            Lang.get("menu.tag.tagFail.txt")
                                    .getFirst(),
                            1, new ArrayList<>());

                    inventory.setItem(inventorySlot, tagButton);

                    break;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
                String username = offlinePlayer.getName();
                int usercount = TagManager.getUsercountById(tagIndex);
                String tagText = tag.getTagString();

                ItemStack tagButton = Utils.getUiButton(Utils.createCustomHeadItem(offlinePlayer),
                        Lang.get("menu.tag.my.tag.txt",
                                Placeholder.parsed("tag", tagText),
                                Placeholder.parsed("ownername", username))
                                .getFirst(),
                        1, Lang.get("menu.tag.my.tag.desc",
                                Placeholder.parsed("id", String.valueOf(tagIndex)),
                                Placeholder.parsed("ownername", username),
                                Placeholder.parsed("usercount", String.valueOf(usercount))));

                inventory.setItem(inventorySlot, tagButton);
            }
        }
    }

    @Override
    public void handleClick(int slotId, Player player) {
        switch (slotId) {
            case (9 * 5 + 4):
                TagMenu tagMenu = new TagMenu(player);
                player.openInventory(tagMenu.getInventory());
                break;

            // last page
            case (9 * 5 + 2):
                if (pageIndex <= 0) {
                    break;
                }
                TagMenuMy tagMenuServerlistLastPage = new TagMenuMy(player, pageIndex - 1);
                player.openInventory(tagMenuServerlistLastPage.getInventory());
                break;

            // next page
            case (9 * 5 + 6):
                if (pageIndex >= maxPage) {
                    break;
                }
                TagMenuMy tagMenuServerlistNextPage = new TagMenuMy(player, pageIndex + 1);
                player.openInventory(tagMenuServerlistNextPage.getInventory());
                break;

            default:
                handleTagClick(slotId, player);
                break;
        }
    }

    private void handleTagClick(int slotId, Player player) {
        if (slotId < PAGE_START || slotId > PAGE_START + (9 * PAGE_HEIGHT) - 1 || slotId % 9 == 0 || slotId % 9 == 8)
            return;

        int row = (slotId - PAGE_START) / 9;
        int col = (slotId - PAGE_START) % 9;

        int index = row * PAGE_WIDTH + col + ITEMS_PER_PAGE * pageIndex;

        List<Integer> playersTags = TagManager.getAllActiveTagsForPlayer(this.playerUUID);

        if (index > playersTags.size() - 1)
            return;

        Component result = TagManager.moveTagUp(this.playerUUID, index);

        TagMenuMy tagMenuMy = new TagMenuMy(player, pageIndex);
        player.sendMessage(result);
        player.openInventory(tagMenuMy.getInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}