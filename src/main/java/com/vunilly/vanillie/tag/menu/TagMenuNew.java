package com.vunilly.vanillie.tag.menu;

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
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class TagMenuNew implements ClickMenu {
    private Inventory inventory;
    private UUID playerUUID;
    public ArrayList<String> colorList = new ArrayList<>();
    public String tagText = "";

    public TagMenuNew(Player player, ArrayList<String> colorList, String text) {
        this.inventory = Bukkit.createInventory(this, 27, Lang.get("menu.tag.new.title").getFirst());
        this.playerUUID = player.getUniqueId();
        this.colorList = colorList;
        this.tagText = text;
        setupInventory();
    }

    public void setupInventory() {
        Utils.decorateInventory(Material.LIGHT_BLUE_STAINED_GLASS_PANE, inventory, 3);

        ItemStack colorButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3YzU0ZmY3ODYyMTE2ZTY1YTE0MzY2MjBiOTFhZjU4YjUyYWIxNzE1MmExODM3MTgwZjM0NTgwMzJmNTcwMiJ9fX0"),
                Lang.get("menu.tag.new.color.txt").getFirst(), 1, Lang.get("menu.tag.new.color.desc"));
        inventory.setItem(9 + 1, colorButton);

        ItemStack textButton = Utils.getUiButton(new ItemStack(Material.PALE_OAK_SIGN),
                Lang.get("menu.tag.new.text.txt").getFirst(), 1, Lang.get("menu.tag.new.text.desc"));
        inventory.setItem(9 + 3, textButton);

        ItemStack previewButton = Utils.getUiButton(Utils.createCustomHeadItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM1YmEzOTNiODYxMGI2M2ViZWU0YzEzYzgzNThiYzZjOTRhOWRlZGM4ZTRkN2QzNmI5MjIyNTdlNjVlOCJ9fX0"),
                Lang.get("menu.tag.new.preview.txt").getFirst(), 1, Lang.get("menu.tag.new.preview.descs", Placeholder.parsed("tag", getBuildTag())));
        inventory.setItem(9 + 5, previewButton);

        if (this.colorList.size() < 1 || this.tagText.isBlank() || this.tagText.isEmpty()) {
            ItemStack cantConfirmButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVhMzc1NWE2ZmUwMTlhMTczY2UzYTQzMDcwNDUyZTc2Nzc2OGQ1NzU1OWQwNGI3M2UyMWI5MDNlYWExYmQ4MiJ9fX0="),
                    Lang.get("menu.tag.new.cantSave.txt").getFirst(), 1, Lang.get("menu.tag.new.cantSave.desc"));
            inventory.setItem(9 + 7, cantConfirmButton);
        } else {
            ItemStack confirmButton = Utils.getUiButton(Utils.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0="),
                    Lang.get("menu.tag.new.save.txt").getFirst(), 1, Lang.get("menu.tag.new.save.desc"));
            inventory.setItem(9 + 7, confirmButton);
        }

        ItemStack backButton = Utils.getUiButton(new ItemStack(Material.ARROW),
                Lang.get("menu.backCancel").getFirst(), 1, new ArrayList<>());
        inventory.setItem(9 * 2 + 4, backButton);
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        switch (slotId) {
            case (9 + 1):
                TagMenuColorBuilder tagMenuColorSelector = new TagMenuColorBuilder(player, this.colorList,
                        this.tagText);
                player.openInventory(tagMenuColorSelector.getInventory());
                break;

            case (9 + 3):
                openInput(player, this.colorList);
                break;

            case (9 + 7):
                if (this.colorList.size() < 1 || this.tagText.isBlank() || this.tagText.isEmpty()) {
                    player.sendMessage(Lang.get("msg.tag.new.cantSave").getFirst());
                } else {
                    Component result = TagManager.createTag(player.getUniqueId(), getBuildTag());
                    player.sendMessage(result);
                    player.closeInventory();
                }
                break;

            case (9 * 2 + 4):
                TagMenu tagMenu = new TagMenu(player);
                player.openInventory(tagMenu.getInventory());
                break;

            default:
                break;
        }
    }

    public static void openInput(Player player, ArrayList<String> colorHexCodeList) {
        player.closeInventory();

        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);

        // Öffne das SignUI
        plugin.getSignUI().open(player, new Component[] {
                Component.text(""),
                Component.text(""),
                Component.text("^^^^^^^^^^"),
                Component.text(Lang.getString("menu.tag.new.signUi.1"))
        }, (inputLines) -> {
            String text = inputLines[0] + inputLines[1];
            if (text != null) {
                if (text.isBlank()) {
                    player.sendMessage(Lang.get("msg.tag.new.didntSaveText").getFirst());
                } else {
                    player.sendMessage(Lang.get("msg.tag.new.savedText", Placeholder.parsed("text", text)).getFirst());
                }
            }

            TagMenuNew t = new TagMenuNew(player, colorHexCodeList, text);
            player.openInventory(t.getInventory());
        });
    }

    public String getBuildTag() {
        StringBuilder builder = new StringBuilder();
        builder.append("<!italic><!b><b>");
        if (this.colorList.size() == 0) {
            // no colors
            builder.append(this.tagText);
        } else if (this.colorList.size() >= 2) {
            // more then 2 colors = gradient
            builder.append("<gradient");
            for (String hexColor : this.colorList) {
                builder.append(":");
                builder.append(hexColor);
            }
            builder.append(">");
            builder.append(this.tagText);
            builder.append("</gradient>");
        } else {
            // single color
            builder.append("<c:");
            builder.append(this.colorList.get(0));
            builder.append(">");
            builder.append(this.tagText);
            builder.append("</c>");
        }
        return builder.toString();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
