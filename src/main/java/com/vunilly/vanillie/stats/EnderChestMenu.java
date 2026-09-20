package com.vunilly.vanillie.stats;

import com.vunilly.vanillie.utils.ClickMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.querz.nbt.io.NamedTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.io.NBTUtil;

public class EnderChestMenu implements ClickMenu {
    private Inventory inventory;
    private UUID senderUUID;
    private UUID playerUUID;
    private String name;

    public EnderChestMenu(Player player, String getFromPlayerName, UUID playerUUID) {
        this.inventory = Bukkit.createInventory(player, 54, Lang.get("menu.stats.enderchest.title",
                Placeholder.parsed("name", getFromPlayerName)
        ).getFirst());
        this.senderUUID = player.getUniqueId();
        this.playerUUID = playerUUID;
        this.name = getFromPlayerName;
        setupInventory();
    }

    private void setupInventory() {
        inventory.clear();

        try {
            File playerFile = new File("world/players/data/" + playerUUID + ".dat");
            NamedTag playerTag = NBTUtil.read(playerFile);
            CompoundTag playerData = (CompoundTag) playerTag.getTag();

            ListTag<?> inventoryData = playerData.getListTag("EnderItems");
            for (int i = 0; i < inventoryData.size(); i++) {
                CompoundTag itemTag = (CompoundTag) inventoryData.get(i);
                String itemId = itemTag.getString("id").replace("minecraft:", "").toUpperCase();
                byte itemPos = itemTag.getByte("Slot");
                int itemCount = itemTag.getInt("count");
                String name = itemTag.getString("name");

                Material material =  Material.getMaterial(itemId);
                if (material == null) material = Material.ACACIA_BOAT;

                ItemStack item = Utils.getUiButton(
                        new ItemStack(material),
                        Component.text(itemId),
                        itemCount,
                        new ArrayList<>());

                inventory.setItem((int) itemPos, item);
            }

        } catch (Exception e) {
            Bukkit.getPlayer(senderUUID).sendMessage("msg.stats.error");
            Bukkit.getLogger().warning("[Vanillie] An error occured while trying to get Ender Chests contents: " + e);
        }
    }

    @Override
    public void handleClick(int slotId, ClickType clickType, Player player) {
        //
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
