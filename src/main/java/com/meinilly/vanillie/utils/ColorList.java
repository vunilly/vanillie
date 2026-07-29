package com.meinilly.vanillie.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class ColorList implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private final static MiniMessage minimessage = MiniMessage.miniMessage();
    public int pageIndex;
    public int maxPage;
    private static final int PAGE_WIDTH = 7;
    private static final int PAGE_HEIGHT = 4;
    private static final int ITEMS_PER_PAGE = PAGE_WIDTH * PAGE_HEIGHT;
    private static final int PAGE_START = 9 + 1; // erste Item-Position
    public String selectedHex;
    private Consumer<String> onColorSelected;
    private Runnable onBack;

    public ColorList(Player player, int pageIndex, Consumer<String> onColorSelected, Runnable onBack) {
        this.onColorSelected = onColorSelected;
        this.onBack = onBack;

        this.pageIndex = pageIndex;
        this.maxPage = (int) Math.ceil((double) Colors.COLORS.size() / ITEMS_PER_PAGE) - 1;

        this.inventory = Bukkit.createInventory(this, 54,
                Lang.get("menu.colorList.title", 
                Placeholder.parsed("page", String.valueOf(this.pageIndex+1)), 
                Placeholder.parsed("maxpage", String.valueOf(this.maxPage+1))
                ).getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 6);

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW), Lang.get("menu.back").getFirst(), 1,
                new ArrayList<>());
        inventory.setItem(9 * 5 + 4, backButton);

        if (pageIndex > 0) {
            ItemStack lastPageButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNzg0NTFiZjI2Y2Y0OWZkNWY1NGNkOGYyYjM3Y2QyNWM5MmU1Y2E3NjI5OGIzNjM0Y2I1NDFlOWFkODkifX19"),
                    Lang.get("menu.page.last").getFirst(), pageIndex, new ArrayList<>());
            inventory.setItem(9 * 5 + 2, lastPageButton);
        }

        if (pageIndex < maxPage) {
            ItemStack nextPageButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE3ZjM2NjZkM2NlZGZhZTU3Nzc4Yzc4MjMwZDQ4MGM3MTlmZDVmNjVmZmEyYWQzMjU1Mzg1ZTQzM2I4NmUifX19"),
                    Lang.get("menu.page.next").getFirst(), pageIndex + 2, new ArrayList<>());
            inventory.setItem(9 * 5 + 6, nextPageButton);
        }

        // Farben anzeigen
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int colorCount = Colors.COLORS.size();

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int row = i % PAGE_WIDTH;
            int col = (i / PAGE_WIDTH) * 9;

            int inventorySlot = PAGE_START + row + col;
            int colorIndex = startIndex + i;

            // Erster Slot auf jeder Seite für Custom-Farbe
            if (pageIndex == 0 && i == 0) {
                ItemStack customColorButton = Utils.getUiButton(new ItemStack(Material.NETHER_STAR),
                        Lang.get("menu.colorList.createColor.txt").getFirst(), 1, Lang.get("menu.colorList.createColor.desc"));
                inventory.setItem(inventorySlot, customColorButton);
            }
            // Normale Farben
            else if (colorIndex < colorCount) {
                int firstPageOffset = 0;
                if (this.pageIndex == 0) {
                    firstPageOffset = 1;
                }
                Map.Entry<String, String> entry = Colors.getEntryByIndex(colorIndex - firstPageOffset);

                if (entry == null)
                    break;

                ItemStack colorButton = Utils.getUiButton(Utils.createCustomHeadItem(entry.getValue()),
                        Lang.get("menu.colorList.color.txt",
                            Placeholder.parsed("hexcode", entry.getKey())
                        ).getFirst(),
                        1, Lang.get("menu.colorList.color.desc", 
                        Placeholder.parsed("hexcode", entry.getKey())));

                inventory.setItem(inventorySlot, colorButton);
            }
        }
    }

    @Override
    public void handleClick(int slotId, Player player) {
        switch (slotId) {
            // back
            case (9 * 5 + 4):
                this.onBack.run();
                break;

            // last page
            case (9 * 5 + 2):
                if (pageIndex <= 0) {
                    break;
                }
                ColorList tagMenuColorLastPage = new ColorList(player, pageIndex - 1,
                        this.onColorSelected, this.onBack);
                player.openInventory(tagMenuColorLastPage.getInventory());
                break;

            // next page
            case (9 * 5 + 6):
                if (pageIndex >= maxPage) {
                    break;
                }
                ColorList tagMenuColorNextPage = new ColorList(player, pageIndex + 1,
                        this.onColorSelected, this.onBack);
                player.openInventory(tagMenuColorNextPage.getInventory());
                break;
            default:
                handleColorClick(slotId, player);
                break;
        }
    }

    private void handleColorClick(int slotId, Player player) {
        if (slotId < PAGE_START || slotId > PAGE_START + (9 * PAGE_HEIGHT) - 1 || slotId % 9 == 0 || slotId % 9 == 8)
            return;

        int row = (slotId - PAGE_START) / 9;
        int col = (slotId - PAGE_START) % 9;

        int index = row * PAGE_WIDTH + col + ITEMS_PER_PAGE * pageIndex;
        if (pageIndex == 0)
            index--;

        if (index > Colors.getColorCount() - 1)
            return;

        Map.Entry<String, String> entry = Colors.getEntryByIndex(index);
        this.onColorSelected.accept(entry.getKey());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
