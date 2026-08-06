package com.vunilly.vanillie.twitch;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.tag.menu.TagMenuNew;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TwitchMenu implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;

    public TwitchMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.twitch.title").getFirst());
        this.playerUUID = player.getUniqueId();
        setupInventory();
    }

    private void setupInventory() {
        inventory.clear();

        Utils.decorateInventory(Material.PURPLE_STAINED_GLASS_PANE, inventory, 3);

        ItemStack twitchButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1ODA0ODliMmQ0NGU1ZDlhOWIzZjgzNmVmMjE5ZjAzMTI5OTJkNDBiMTRkOTlmNTZjNWFmMDVjNDBmNzE1In19fQ"),
                Lang.get("menu.twitch.select").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 + 4, twitchButton);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case 9 + 4:
                Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);

                plugin.getSignUI().open(player, new Component[] {
                        Component.text(""),
                        Component.text(""),
                        Component.text("^^^^^^^^^^"),
                        Component.text(Lang.getString("menu.twitch.signUi.1"))
                }, (inputLines) -> {
                    String text = inputLines[0] + inputLines[1];
                    if (text != null) {
                        if (text.isBlank()) {
                            player.sendMessage(Lang.get("msg.twitch.didntSaveText").getFirst());
                        } else {
                            player.sendMessage(
                                    Lang.get("msg.twitch.savedText", Placeholder.parsed("text", text)).getFirst());
                        }
                    }

                    TwitchManager.setTwitchUsername(player.getUniqueId(), text);
                });
                break;

            default:
                break;
        }
    }

}
