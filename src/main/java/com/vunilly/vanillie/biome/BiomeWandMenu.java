package com.vunilly.vanillie.biome;

import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.utils.ClickMenu;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BiomeWandMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    int selectedBiome = 0;

    public BiomeWandMenu(UUID playerUUID) {
        this.selectedBiome = BiomeManager.getBiomeId(playerUUID)+1;

        String biome = BiomeManager.getBiomeById(selectedBiome-1);

        String currentBiome = Lang.getString("menu.biomewand.biome." + biome);

        this.inventory = Bukkit.createInventory(
                this,
                36,
                Lang.get(
                        "menu.biomewand.title",
                        Placeholder.parsed("current", currentBiome)
                ).getFirst()
        );
        this.playerUUID = playerUUID;
        setupInventory();
    }

    public void setupInventory() {
        inventory.clear();

        Utils.decorateInventory(Material.LIME_STAINED_GLASS_PANE, inventory, 4);

        AtomicInteger counter = new AtomicInteger();
        BiomeManager.biomes.forEach((biomeKey, material) -> {
            int pos = (int) (9 + 1 + counter.get() % 7 + Math.floor((double) counter.get() / 7) * 9);
            counter.incrementAndGet();

            ItemStack biomeButton = Utils.getUiButton(
                    new ItemStack(material),
                    Lang.get("menu.biomewand.biome." + biomeKey).getFirst(),
                    1,
                    new ArrayList<>(),
                    counter.get() == selectedBiome
            );
            inventory.setItem(pos, biomeButton);

            if (counter.get() == selectedBiome) {
                if (pos < 2*9) {
                    inventory.setItem(pos-9, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                } else {
                    inventory.setItem(pos+9, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }
            }
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        if (clickType != ClickType.LEFT) {
            return;
        }

        int row = slotId / 9;
        int column = slotId % 9;

        if (row != 1 && row != 2) {
            return;
        }

        if (column == 0 || column == 8) {
            return;
        }

        int biomeId = (row - 1) * 7 + (column - 1);

        BiomeManager.setBiome(player.getUniqueId(), biomeId);

        String biome = BiomeManager.getBiomeById(biomeId);

        if (biome == null) {
            return;
        }

        player.sendMessage(
                Lang.get(
                        "msg.biomewand.selectedBiome",
                        Placeholder.component("biome", Lang.get("menu.biomewand.biome."+biome).getFirst())
                ).getFirst()
        );

        BiomeWandMenu menu = new BiomeWandMenu(player.getUniqueId());
        player.openInventory(menu.getInventory());
    }
}
