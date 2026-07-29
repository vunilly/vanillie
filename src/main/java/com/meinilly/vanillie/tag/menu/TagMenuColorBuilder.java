package com.meinilly.vanillie.tag.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.utils.ClickMenu;
import com.meinilly.vanillie.utils.Colors;
import com.meinilly.vanillie.utils.Lang;
import com.meinilly.vanillie.utils.ColorList;
import com.meinilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagMenuColorBuilder implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private final static MiniMessage minimessage = MiniMessage.miniMessage();
    private static final int PAGE_WIDTH = 7;
    private static final int PAGE_HEIGHT = 3;
    private static final int ITEMS_PER_PAGE = PAGE_WIDTH * PAGE_HEIGHT;
    private static final int PAGE_START = 9 + 1; // erste Item-Position
    public ArrayList<String> colorList = new ArrayList<>();
    public String tagText = "";

    public TagMenuColorBuilder(Player player, ArrayList<String> colorList, String tagText) {
        this.inventory = Bukkit.createInventory(this, 45, Lang.get("menu.tag.colorBuilder.title").getFirst());
        this.playerUUID = player.getUniqueId();
        this.colorList = colorList;
        this.tagText = tagText;
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 5);

        int counter = 0;
        for (String colorHexCode : colorList) {
            int row = counter % 7;
            int col = (counter / 7) * 9;

            int inventorySlot = 9 + 1 + row + col;

            ItemStack colorStack;

            String base64String = Colors.COLORS.get(colorHexCode);
            if (base64String == null) {
                colorStack = new ItemStack(Material.PAPER);
            } else {
                colorStack = Utils.createCustomHeadItem(base64String);
            }

            ItemStack colorButton = Utils.getUiButton(colorStack,
                    Lang.get("menu.tag.colorBuilder.color.txt", Placeholder.parsed("hexcode", colorHexCode)).getFirst(),
                    counter + 1, Lang.get("menu.tag.colorBuilder.color.desc", Placeholder.parsed("hexcode", colorHexCode)));
            inventory.setItem(inventorySlot, colorButton);
            counter++;
        }

        int colorListSize = colorList.size();
        int row = colorListSize % 7;
        int col = (colorListSize / 7) * 9;

        int inventorySlot = 9 + 1 + row + col;

        if (colorListSize < 3*7) {
            ItemStack addColorButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBiNTVmNzQ2ODFjNjgyODNhMWMxY2U1MWYxYzgzYjUyZTI5NzFjOTFlZTM0ZWZjYjU5OGRmMzk5MGE3ZTcifX19"),
                Lang.get("menu.tag.colorBuilder.addColor.txt").getFirst(), 1, new ArrayList<>());
            inventory.setItem(inventorySlot, addColorButton);
        }

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW), Lang.get("menu.backSave").getFirst(), 1,
                new ArrayList<>());
        inventory.setItem(9 * 4 + 4, backButton);
    }

    @Override
    public void handleClick(int slotId, Player player) {
        switch (slotId) {
            case 9 * 4 + 4:
                TagMenuNew tagMenuNewRetainingData = new TagMenuNew(player, this.colorList, this.tagText);
                player.openInventory(tagMenuNewRetainingData.getInventory());
                break;

            default:
                handleAddColorClick(slotId, player);
                handleColorClick(slotId, player);
                break;
        }
    }

    private void handleColorClick(int slotId, Player player) {
        if (slotId < PAGE_START || slotId > PAGE_START + 9 * PAGE_HEIGHT || slotId % 9 == 0 || slotId % 9 == 8)
            return;

        int row = (slotId - PAGE_START) / 9;
        int col = (slotId - PAGE_START) % 9;

        int index = row * PAGE_WIDTH + col;

        if (index > this.colorList.size() - 1)
            return;

        this.colorList.remove(index);

        TagMenuColorBuilder tagMenuColorBuilder = new TagMenuColorBuilder(player, this.colorList, this.tagText);
        player.openInventory(tagMenuColorBuilder.getInventory());
    }

    private void handleAddColorClick(int slotId, Player player) {
        int colorListSize = this.colorList.size();
        int row = colorListSize % PAGE_WIDTH;
        int col = (colorListSize / PAGE_WIDTH) * 9;

        int addColorButtonSlot = 9 + 1 + row + col;

        if (colorListSize < PAGE_HEIGHT*PAGE_WIDTH) {
            if (slotId == addColorButtonSlot) {
                ColorList tagMenuColorList = new ColorList(
                        player,
                        0,
                        (gewaehlterHex) -> {
                            this.colorList.add(gewaehlterHex);
                            TagMenuColorBuilder neu = new TagMenuColorBuilder(player, this.colorList, this.tagText);
                            player.openInventory(neu.getInventory());
                            player.sendMessage(Lang.get("msg.tag.colorBuilder.savedAllColors").getFirst());
                        },
                        () -> {
                            TagMenuColorBuilder neu = new TagMenuColorBuilder(player, this.colorList, this.tagText);
                            player.openInventory(neu.getInventory());
                        });
                player.openInventory(tagMenuColorList.getInventory());
                return;
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
