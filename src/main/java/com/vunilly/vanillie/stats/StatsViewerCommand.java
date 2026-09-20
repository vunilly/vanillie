package com.vunilly.vanillie.stats;

import com.vunilly.vanillie.tag.menu.TagMenu;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatsViewerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.getString("msg.onlyPlayersAllowed"));
            return true;
        }

        if (args.length <= 1) {
            player.sendMessage(Lang.get("msg.tooFewParams", Placeholder.parsed("command", command.getName())).getFirst());
            return true;
        } else if (args.length > 2) {
            player.sendMessage(Lang.get("msg.tooManyParams", Placeholder.parsed("command", command.getName())).getFirst());
            return true;
        }

        String playerName = args[1];

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        UUID playerUUID = offlinePlayer.getUniqueId();

        switch(args[0]) {
            case "enderchest":
                EnderChestMenu enderChestMenu = new EnderChestMenu(player, playerName, playerUUID);
                player.openInventory(enderChestMenu.getInventory());
                break;
            case "inventory":
                InventoryMenu inventory = new InventoryMenu(player, playerName, playerUUID);
                player.openInventory(inventory.getInventory());
                break;
            default:
                player.sendMessage(Lang.get("msg.unknownParam").getFirst());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("enderchest".startsWith(args[0].toLowerCase())) {
                completions.add("enderchest");
            }

            if ("inventory".startsWith(args[0].toLowerCase())) {
                completions.add("inventory");
            }

        }

        return completions;
    }
}