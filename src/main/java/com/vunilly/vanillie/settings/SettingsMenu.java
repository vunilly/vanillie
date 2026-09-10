package com.vunilly.vanillie.settings;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.pvp.PvpManager;
import com.vunilly.vanillie.pvp.PvpMenu;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

public class SettingsMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    public SettingsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, Lang.get("menu.settings.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.BLUE_STAINED_GLASS_PANE, inventory, 6);

        ItemStack maxTagsCreateButton = Utils.getUiButton(new ItemStack(Material.NAME_TAG),
                Lang.get("menu.settings.tag.maxPlayer.txt").getFirst(), 1, Lang.get("menu.settings.tag.maxPlayer.desc"));
        inventory.setItem(9 + 1, maxTagsCreateButton);

        ItemStack maxTagsActiveButton = Utils.getUiButton(new ItemStack(Material.NAME_TAG),
                Lang.get("menu.settings.tag.maxActive.txt").getFirst(), 1, Lang.get("menu.settings.tag.maxActive.desc"));
        inventory.setItem(9 + 2, maxTagsActiveButton);

        ItemStack voteDaytimeEnabledButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg5MDQyMDgyYmI3YTc2MThiNzg0ZWU3NjA1YTEzNGM1ODgzNGUyMWUzNzRjODg4OTM3MTYxMDU3ZjZjNyJ9fX0"),
                Lang.get("menu.settings.vote.enableDaytimes.txt").getFirst(), 1, Lang.get("menu.settings.vote.enableDaytimes.desc"));
        inventory.setItem(9 + 3, voteDaytimeEnabledButton);

        ItemStack voteWeatherEnabledButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5MmQxNzI2MTcxYWJhYmY3M2Y4NDQxMTU0Y2Y3YjcyZWUyZTBlNDY0NGQ2ZWUwODM4ZDc2MGRjMzQ4OWM5MiJ9fX0"),
                Lang.get("menu.settings.vote.enableWeather.txt").getFirst(), 1, Lang.get("menu.settings.vote.enableWeather.desc"));
        inventory.setItem(9 + 4, voteWeatherEnabledButton);

        ItemStack twitchEnabledButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1ODA0ODliMmQ0NGU1ZDlhOWIzZjgzNmVmMjE5ZjAzMTI5OTJkNDBiMTRkOTlmNTZjNWFmMDVjNDBmNzE1In19fQ"),
                Lang.get("menu.settings.enableTwitch.txt").getFirst(), 1, Lang.get("menu.settings.enableTwitch.desc"));
        inventory.setItem(9 + 5, twitchEnabledButton);

        ItemStack minSizeButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0"),
                Lang.get("menu.settings.size.minSize.txt").getFirst(), 1, Lang.get("menu.settings.size.minSize.desc"));
        inventory.setItem(9 + 6, minSizeButton);

        ItemStack maxSizeButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIyMWRhNDQxOGJkM2JmYjQyZWI2NGQyYWI0MjljNjFkZWNiOGY0YmY3ZDRjZmI3N2ExNjJiZTNkY2IwYjkyNyJ9fX0"),
                Lang.get("menu.settings.size.maxSize.txt").getFirst(), 1, Lang.get("menu.settings.size.maxSize.desc"));
        inventory.setItem(9 + 7, maxSizeButton);

        ItemStack endermanEnabledButton = Utils.getUiButton(new ItemStack(Material.ENDERMAN_SPAWN_EGG),
                Lang.get("menu.settings.enableEnderman.txt").getFirst(), 1, Lang.get("menu.settings.enableEnderman.desc"));
        inventory.setItem(9*2 + 1, endermanEnabledButton);

        ItemStack endEnabledButton = Utils.getUiButton(new ItemStack(Material.END_PORTAL_FRAME),
                Lang.get("menu.settings.enableEnd.txt").getFirst(), 1, Lang.get("menu.settings.endableEnd.desc"));
        inventory.setItem(9*2 + 2, endEnabledButton);
        
        ItemStack pvpTimerButton = Utils.getUiButton(new ItemStack(Material.IRON_SWORD),
                Lang.get("menu.settings.pvp.timer.txt").getFirst(), 1, Lang.get("menu.settings.pvp.timer.desc"));
        inventory.setItem(9*2 + 3, pvpTimerButton);

        ItemStack pvpEnabledButton = Utils.getUiButton(new ItemStack(Material.IRON_SWORD),
                Lang.get("menu.settings.pvp.enabled.txt").getFirst(), 1, Lang.get("menu.settings.pvp.enabled.desc"));
        inventory.setItem(9*2 + 4, pvpEnabledButton);

        ItemStack xpEnabledButton = Utils.getUiButton(new ItemStack(Material.EXPERIENCE_BOTTLE),
                Lang.get("menu.settings.xpEnabled.txt").getFirst(), 1, Lang.get("menu.settings.xpEnabled.desc"));
        inventory.setItem(9*2 + 5, xpEnabledButton);

        ItemStack biomeWandEbabledButton = Utils.getUiButton(new ItemStack(Material.GRASS_BLOCK),
                Lang.get("menu.settings.biomeWandEnabled.txt").getFirst(), 1, Lang.get("menu.settings.biomeWandEnabled.desc"));
        inventory.setItem(9*2 + 6, biomeWandEbabledButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 + 4):

                break;

            default:
                break;
        }
    }

}
