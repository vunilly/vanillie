package com.vunilly.vanillie.namecolor;

import com.vunilly.vanillie.tag.menu.TagMenuColorBuilder;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.ColorList;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class NameColorMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    public NameColorMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.namecolor.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventoryWithRainbow(this.inventory, 3);

        ItemStack resetColorButton = Utils.getUiButton(Utils.createCustomHeadItem(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3NDU1ZGFlOTY5ODgyM2U0MDg3MjA5Y2M4ZTc2NDMxOWViYzMzNDhmN2ZhYmFhOWE4MTU1OTRmYzY0ZSJ9fX0"),
                Lang.get("menu.namecolor.reset").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 3, resetColorButton);

        ItemStack setColorButton = Utils.getUiButton(Utils.createCustomHeadItem(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3YzU0ZmY3ODYyMTE2ZTY1YTE0MzY2MjBiOTFhZjU4YjUyYWIxNzE1MmExODM3MTgwZjM0NTgwMzJmNTcwMiJ9fX0"),
                Lang.get("menu.namecolor.set").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 5, setColorButton);
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 + 3):
                NameColorManager.resetColor(player.getUniqueId());
                player.closeInventory();
                return;
            case (9 + 5):
                ColorList tagMenuColorList = new ColorList(
                        player,
                        0,
                        (chosenHex) -> {
                            NameColorManager.setColor(player.getUniqueId(), chosenHex);
                            NameColorMenu nameColorMenu = new NameColorMenu(player);
                            player.openInventory(nameColorMenu.getInventory());
                        },
                        () -> {
                            NameColorMenu nameColorMenu = new NameColorMenu(player);
                            player.openInventory(nameColorMenu.getInventory());
                        });
                player.openInventory(tagMenuColorList.getInventory());
            return;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
