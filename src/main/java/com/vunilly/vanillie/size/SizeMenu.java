package com.vunilly.vanillie.size;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.pvp.PvpManager;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class SizeMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    private double MAX_SIZE = 3.0;
    private double MIN_SIZE = 0.1;

    public SizeMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.size.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        inventory.clear();
        
        double current = 1.0;
        Player player = Bukkit.getPlayer(playerUUID);

        if (player != null) {
            AttributeInstance scale = player.getAttribute(Attribute.SCALE);
            if (scale != null) {
                current = scale.getBaseValue();
            }
        }

        int heightCm = (int) Math.round(180 * current);

        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, this.inventory, 3);

        if (((double) (heightCm - 1) / 180.0) >= MIN_SIZE) {
            ItemStack sizeMinusCmButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBkMWRmODA0NmYwYjVkOTM0YzNlMDU3OThlYWNmZWVhNmQ3YjU5NWRiZTI2ZGViZjdkYjlhY2M4YzRmYTc5OCJ9fX0"),
                    Lang.get("menu.size.minuscm").getFirst(), 1, new ArrayList<>());
            inventory.setItem(9 + 1, sizeMinusCmButton);
        }
        if (current - 0.1 >= MIN_SIZE) {
            ItemStack sizeMinusButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBkMWRmODA0NmYwYjVkOTM0YzNlMDU3OThlYWNmZWVhNmQ3YjU5NWRiZTI2ZGViZjdkYjlhY2M4YzRmYTc5OCJ9fX0"),
                    Lang.get("menu.size.minus").getFirst(), 1, new ArrayList<>());
            inventory.setItem(9 + 2, sizeMinusButton);
        }

        ItemStack currentButton = Utils.getUiButton(new ItemStack(Material.PALE_OAK_SIGN),
                Lang.get("menu.size.current.txt",
                        Placeholder.parsed("current", String.format("%.1f", current))).getFirst(),
                1,
                Lang.get("menu.size.current.desc",
                        Placeholder.parsed("incm", String.valueOf(heightCm))));
        inventory.setItem(9 + 4, currentButton);

        if (((double) (heightCm + 1) / 180.0) <= MAX_SIZE) {
            ItemStack sizePlusCmButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmYzIzODY2NTIzY2FhYThhOTUzNDU2NjEyN2E2ZjgzODlhZjNlNzZiOGUzYzMzYzI0NzNjYmE2ODg5YzQifX19"),
                    Lang.get("menu.size.pluscm").getFirst(), 1, new ArrayList<>());
            inventory.setItem(9 + 7, sizePlusCmButton);
        }
        
        if (current + 0.1 <= MAX_SIZE) {
            ItemStack sizePlusButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmYzIzODY2NTIzY2FhYThhOTUzNDU2NjEyN2E2ZjgzODlhZjNlNzZiOGUzYzMzYzI0NzNjYmE2ODg5YzQifX19"),
                    Lang.get("menu.size.plus").getFirst(), 1, new ArrayList<>());
            inventory.setItem(9 + 6, sizePlusButton);
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        AttributeInstance scale = player.getAttribute(Attribute.SCALE);

        if (scale == null) {
            player.sendMessage(Lang.get("size.error").getFirst());
            return;
        }

        double current = scale.getBaseValue();
        int heightCm = (int) Math.round(180 * current);

        switch (slotId) {
            case (9 + 1):
                heightCm -= 1;
                current = (double) heightCm / 180.0;

                if (current < MIN_SIZE) {
                    return;
                }

                scale.setBaseValue(current);
                setupInventory();
                break;

            case (9 + 2):
                current -= 0.1;

                if (current < MIN_SIZE) {
                    return;
                }

                scale.setBaseValue(current);
                setupInventory();
                break;

            case (9 + 6):
                current += 0.1;

                if (current > MAX_SIZE) {
                    return;
                }

                scale.setBaseValue(current);
                setupInventory();
                break;

            case (9 + 7):
                heightCm += 1;
                current = (double) heightCm / 180.0;

                if (current > MAX_SIZE) {
                    return;
                }

                scale.setBaseValue(current);
                setupInventory();
                break;
        }
    }
}
