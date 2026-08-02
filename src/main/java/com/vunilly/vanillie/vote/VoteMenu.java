package com.vunilly.vanillie.vote;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.pvp.PvpCommand;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

public class VoteMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    public VoteMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.vote.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.WHITE_STAINED_GLASS_PANE, inventory, 3);

        ItemStack clearWeatherButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWY1NzJiNmMwMTc4ZTAxZWM3YzVlMTdiNDE2YzZmYmRjZmJjN2ExYTcxY2FhZDVmMmVkNjZkYzM4NDNjNjRmIn19fQ"),
                Lang.get("menu.vote.type.clear").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 2, clearWeatherButton);
        
        ItemStack rainWeatherButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5MmQxNzI2MTcxYWJhYmY3M2Y4NDQxMTU0Y2Y3YjcyZWUyZTBlNDY0NGQ2ZWUwODM4ZDc2MGRjMzQ4OWM5MiJ9fX0"),
                Lang.get("menu.vote.type.rain").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 3, rainWeatherButton);

        ItemStack thunderWeatherButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE5NTcwNDE1Zjk0YjM5NGZmNTFhOTI1OWYxZmNmOWRiMzA2Njc3NDM4YmRjOGJhYzM1ZGNkNTkxYWEwMmVkZSJ9fX0"),
                Lang.get("menu.vote.type.thunder").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 4, thunderWeatherButton);

        ItemStack nightButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJhMTY1MmUxOWRmZDliZTVjYzE2ODcyOTNhNDFkODEwNDkyZWU4NzEyZmIyMzIzYTBjNjkyNDYzMzQzYTBhNyJ9fX0"),
                Lang.get("menu.vote.type.night").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 5, nightButton);

        ItemStack dayButton = Utils.getUiButton(new ItemStack(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg5MDQyMDgyYmI3YTc2MThiNzg0ZWU3NjA1YTEzNGM1ODgzNGUyMWUzNzRjODg4OTM3MTYxMDU3ZjZjNyJ9fX0")),
                Lang.get("menu.vote.type.day").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 6, dayButton);
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 + 2):
                VoteManager.startWish(0, player.getUniqueId());
                player.closeInventory();
                break;

            case (9 + 5):
                VoteManager.startWish(1, player.getUniqueId());
                player.closeInventory();
                break;

            case (9 + 6):
                VoteManager.startWish(2, player.getUniqueId());
                player.closeInventory();
                break;

            case (9 + 3):
                VoteManager.startWish(3, player.getUniqueId());
                player.closeInventory();
                break;

            case (9 + 4):
                VoteManager.startWish(4, player.getUniqueId());
                player.closeInventory();
                break;

            default:
                break;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
