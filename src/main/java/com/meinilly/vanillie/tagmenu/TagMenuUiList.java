package com.meinilly.vanillie.tagmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.Vanillie;
import com.meinilly.vanillie.commands.tag.Tag;
import com.meinilly.vanillie.commands.tag.TagManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagMenuUiList implements InventoryHolder {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();
    private Inventory inventory;
    private UUID playerUUID;

    public TagMenuUiList(Player player) {
        this.playerUUID = player.getUniqueId();
        this.inventory = Bukkit.createInventory(this, 45,
                miniMessage.deserialize(Vanillie.getGradientText("Tag Liste")));
        setupInventory();
    }

    private void setupInventory() {
        ItemStack backgroundItem = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(Component.text(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, backgroundItem);
            inventory.setItem(4*9 + i, backgroundItem);
        }
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i*9, backgroundItem);
            inventory.setItem(i*9 + 8, backgroundItem);
        }

        List<Tag> tagList = TagManager.getServerTagList();

        int pos = 0;
        for (Tag tag : tagList) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
            String username = offlinePlayer.getName();

            int userCount = TagManager.findTagPlayerCount(tag.getId());

            ItemStack tagButton = Vanillie.createCustomHeadItem(offlinePlayer);
            ItemMeta tagItemMeta = tagButton.getItemMeta();
            tagItemMeta.displayName(miniMessage.deserialize(tag.getTagString()));

            List<Component> tagLore = new ArrayList<>();

            if (TagManager.doesPlayerHaveTag(playerUUID, tag.getId())) {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientTextSecondary("Klicke um den Tag zu deaktivieren.")));
                tagItemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                tagItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientTextSecondary("Klicke um den Tag zu benutzen.")));
            }
            tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Ersteller*in: " + username)));
            tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Benutzer*innen: " + userCount)));
            if (playerUUID == tag.getOwnerUUID()) {
                tagLore.add(miniMessage.deserialize(Vanillie.getImportantText("Dieser Tag gehört dir!")));
            }
            tagItemMeta.lore(tagLore);

            tagButton.setAmount(userCount);
            tagButton.setItemMeta(tagItemMeta);

            inventory.setItem(pos, tagButton);

            pos++;

        }

        // Back Button
        ItemStack backButton = Vanillie.createCustomHeadItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5NzExMjRiZTg5YWM3ZGM5YzkyOWZlOWI2ZWZhN2EwN2NlMzdjZTFkYTJkZjY5MWJmODY2MzQ2NzQ3N2M3In19fQ");
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Letze Seite")));

        backButton.setItemMeta(backButtonMeta);

        inventory.setItem(9*4 + 2, backButton);

        // Back Button
        ItemStack lastButton = new ItemStack(Material.BARRIER);
        ItemMeta lastButtonMeta = lastButton.getItemMeta();
        lastButtonMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Zurück")));

        lastButton.setItemMeta(lastButtonMeta);

        inventory.setItem(9*4 + 4, lastButton);

        // Next Button
        ItemStack nextButton = Vanillie.createCustomHeadItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY3MWM0YzA0MzM3YzM4YTVjN2YzMWE1Yzc1MWY5OTFlOTZjMDNkZjczMGNkYmVlOTkzMjA2NTVjMTlkIn19fQ");
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        nextButtonMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Nächste Seite")));

        nextButton.setItemMeta(nextButtonMeta);

        inventory.setItem(9*4 + 6, nextButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
