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
    private int pageIndex;
    private int pageSize;
    private int maxPage;
    private int minPage;
    private int showTags; // 0=all, 1=created, 2=active

    public TagMenuUiList(Player player) {
        this(player, 0, 0);
    }

    public TagMenuUiList(Player player, int page, int showTags) {
        this.playerUUID = player.getUniqueId();
        this.pageSize = 21;
        this.pageIndex = page;
        this.showTags = showTags;
        player.sendMessage(page + "");

        List<Tag> tagList;
        if (showTags == 0) {
            tagList = TagManager.getServerTagList();
            player.sendMessage("ist0");
        } else if(showTags == 1) {
            tagList = TagManager.getCreatedTagsForPlayer(playerUUID);
            player.sendMessage("ist1");
        } else {
            tagList = TagManager.getActiveTagsForPlayer(playerUUID);
            player.sendMessage("ist2");
        }

        this.maxPage = (int) Math.ceil((double) tagList.size() / pageSize) - 1;

        // Grenzen validieren
        if (this.pageIndex > maxPage) {
            this.pageIndex = maxPage;
        }
        if (this.pageIndex < 0) {
            this.pageIndex = 0;
        }
        player.sendMessage(this.pageIndex + "");
        this.inventory = Bukkit.createInventory(this, 45,
                miniMessage.deserialize(Vanillie.getGradientText("Tag Liste (Seite " + (this.pageIndex + 1) + ")")));
        setupInventory();
    }

    private void setupInventory() {
        ItemStack backgroundItem = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(Component.text(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, backgroundItem);
            inventory.setItem(4 * 9 + i, backgroundItem);
        }
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i * 9, backgroundItem);
            inventory.setItem(i * 9 + 8, backgroundItem);
        }

        List<Tag> tagList = TagManager.getServerTagList();

        int pageStart = this.pageSize * this.pageIndex;
        int pageEnd = this.pageSize * (this.pageIndex + 1);

        if (pageEnd > tagList.size()) {
            pageEnd = tagList.size();
        }

        int inventoryPosition = 0;
        for (int i = pageStart; i < pageEnd; i++) {
            Tag tag = tagList.get(i);

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tag.getOwnerUUID());
            String username = offlinePlayer.getName();

            int userCount = TagManager.findTagPlayerCount(tag.getId());

            ItemStack tagButton = Vanillie.createCustomHeadItem(offlinePlayer);
            ItemMeta tagItemMeta = tagButton.getItemMeta();
            tagItemMeta.displayName(miniMessage.deserialize(tag.getTagString()));

            List<Component> tagLore = new ArrayList<>();

            if (TagManager.doesPlayerHaveTag(playerUUID, tag.getId())) {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientTextSecondary("Klicke um den Tag zu ")
                        + Vanillie.getGradientTextGreen("aktivieren.") + this.pageIndex));
            } else {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientTextSecondary("Klicke um den Tag zu ")
                        + Vanillie.getImportantText("deaktiveren.")));
            }
            tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Ersteller*in: " + username)));
            tagLore.add(miniMessage.deserialize(Vanillie.getGradientText("Benutzer*innen: " + userCount)));
            if (playerUUID.equals(tag.getOwnerUUID())) {
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientTextGreen("Dieser Tag gehört dir!")));
                tagLore.add(miniMessage.deserialize(Vanillie.getGradientTextGreen("Rechtsklicke zum bearbeiten.")));
            } else {
                tagLore.add(miniMessage.deserialize(Vanillie.getImportantText("Dieser Tag gehört nicht dir!")));
            }
            tagItemMeta.lore(tagLore);

            tagButton.setAmount(userCount);
            tagButton.setItemMeta(tagItemMeta);

            inventory.setItem(10 + inventoryPosition % 7 + Math.round(inventoryPosition / 7) * 9, tagButton);

            inventoryPosition++;
        }

        if (this.pageIndex > this.minPage) {
            // Last Button
            ItemStack lastButton = Vanillie.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5NzExMjRiZTg5YWM3ZGM5YzkyOWZlOWI2ZWZhN2EwN2NlMzdjZTFkYTJkZjY5MWJmODY2MzQ2NzQ3N2M3In19fQ");
            ItemMeta lastMeta = lastButton.getItemMeta();
            lastMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Letze Seite")));

            lastButton.setItemMeta(lastMeta);

            inventory.setItem(9 * 4 + 2, lastButton);
        }

        // Back Button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Zurück")));

        backButton.setItemMeta(backMeta);

        inventory.setItem(9 * 4 + 4, backButton);

        if (this.pageIndex < this.maxPage) {
            // Next Button
            ItemStack nextButton = Vanillie.createCustomHeadItem(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY3MWM0YzA0MzM3YzM4YTVjN2YzMWE1Yzc1MWY5OTFlOTZjMDNkZjczMGNkYmVlOTkzMjA2NTVjMTlkIn19fQ");
            ItemMeta nextButtonMeta = nextButton.getItemMeta();
            nextButtonMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Nächste Seite")));

            nextButton.setItemMeta(nextButtonMeta);

            inventory.setItem(9 * 4 + 6, nextButton);
        }

        // Filter Button
        if (this.showTags == 0) {
            ItemStack filterButton = new ItemStack(Material.WHITE_DYE);
            ItemMeta fitlerMeta = filterButton.getItemMeta();
            fitlerMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Filtern")));

            List<Component> filterLore = new ArrayList<>();
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientTextGreen("Alle anzeigen")));
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientText("Nur meine erstellten anzeigen")));
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientText("Nur meine aktiven anzeigen")));

            filterButton.lore(filterLore);

            filterButton.setItemMeta(fitlerMeta);

            inventory.setItem(4, filterButton);
        } else if (this.showTags == 1) {
            ItemStack filterButton = new ItemStack(Material.GREEN_DYE);
            ItemMeta fitlerMeta = filterButton.getItemMeta();
            fitlerMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Filtern")));

            List<Component> filterLore = new ArrayList<>();
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientText("Alle anzeigen")));
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientTextGreen("Nur meine erstellten anzeigen")));
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientText("Nur meine aktiven anzeigen")));

            filterButton.lore(filterLore);

            filterButton.setItemMeta(fitlerMeta);

            inventory.setItem(4, filterButton);
        } else {
            ItemStack filterButton = new ItemStack(Material.PURPLE_DYE);
            ItemMeta fitlerMeta = filterButton.getItemMeta();
            fitlerMeta.displayName(miniMessage.deserialize(Vanillie.getGradientText("Filtern")));

            List<Component> filterLore = new ArrayList<>();
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientText("Alle anzeigen")));
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientText("Nur meine erstellten anzeigen")));
            filterLore.add(miniMessage.deserialize(Vanillie.getGradientTextGreen("Nur meine aktiven anzeigen")));

            filterButton.lore(filterLore);

            filterButton.setItemMeta(fitlerMeta);

            inventory.setItem(4, filterButton);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setFilterOption(int filterOption) {
        this.showTags = filterOption;
    }

    public int getFilterOption() {
        return this.showTags;
    }
}
