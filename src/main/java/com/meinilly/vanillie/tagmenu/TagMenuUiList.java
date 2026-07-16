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
        this.inventory = Bukkit.createInventory(this, 36, miniMessage.deserialize(Vanillie.getGradientText("Neuen Tag erstellen")));
        setupInventory();
    }

    private void setupInventory() {
        List<Tag> tagList = TagManager.getServerTagList();

        int pos = 0;
        for (Tag tag : tagList) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
            String username = offlinePlayer.getName();

            int userCount = TagManager.findTagPlayerCount(tag.getId());

            ItemStack tagButton = new ItemStack(Material.FLOWER_BANNER_PATTERN);
            ItemMeta tagItemMeta = tagButton.getItemMeta();
            tagItemMeta.displayName(miniMessage.deserialize(tag.getTagString()));

            List<Component> tagLore = new ArrayList<>();
            
            if (TagManager.doesPlayerHaveTag(playerUUID, tag.getId())) {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Klicke um den Tag zu deaktivieren.")));
                tagItemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                tagItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Klicke um den Tag zu benutzen.")));
            }
            tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Ersteller*in: " + username)));
            tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Benutzer*innen: " + userCount)));
            if (playerUUID.equals(tag.getOwnerUUID())) {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Dieser Tag gehört dir!")));
            }
            tagItemMeta.lore(tagLore);
            

            tagButton.setAmount(userCount);
            tagButton.setItemMeta(tagItemMeta);
            
            inventory.setItem(pos, tagButton);

            pos++;

        }

        // Back Button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backItemMeta = backButton.getItemMeta();
        backItemMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Zurück")));

        backButton.setItemMeta(backItemMeta);

        inventory.setItem(9*3+4, backButton);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
