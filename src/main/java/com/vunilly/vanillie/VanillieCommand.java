package com.vunilly.vanillie;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.pvp.PvpManager;
import com.vunilly.vanillie.pvp.PvpMenu;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.ArrayList;
import java.util.List;

public class VanillieCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("msg.onlyPlayersAllowed").getFirst());
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(Lang.get("msg.needOp").getFirst());
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Lang.get("msg.tooFewParams").getFirst());
            return true;
        }
        if (args.length > 1) {
            player.sendMessage(Lang.get("msg.tooManyParamsOkay", Placeholder.parsed("command", command.getName())).getFirst());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clearconfig":
                TagManager.clearData();
                PvpManager.clearData();

                sender.sendMessage(Lang.get("msg.confCleared").getFirst());
                break;

            case "reloadconfig":
                TagManager.clearData();
                TagManager.loadData();

                PvpManager.clearData();
                PvpManager.loadData();

                Lang.clearData();
                Lang.loadLang();

                sender.sendMessage(Lang.get("msg.confReloaded").getFirst());
                break;

            case "saveconfig":
                TagManager.saveData();
                PvpManager.saveData();
                sender.sendMessage(Lang.get("msg.confSaved").getFirst());
                break;

            case "resetbook":
                Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);
                NamespacedKey key = new NamespacedKey(plugin, "info_book_version");

                for (Player playerInList : Bukkit.getOnlinePlayers()) {
                    playerInList.getPersistentDataContainer().remove(key);
                }
                sender.sendMessage(Lang.get("msg.bookReset").getFirst());
                break;

            default:
                sender.sendMessage(Lang.get("msg.unknownParam").getFirst());
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("reloadconfig".startsWith(args[0].toLowerCase())) {
                completions.add("reloadconfig");
            }

            if ("saveconfig".startsWith(args[0].toLowerCase())) {
                completions.add("saveconfig");
            }

            if ("clearconfig".startsWith(args[0].toLowerCase())) {
                completions.add("resetbook");
            }

            if ("resetbook".startsWith(args[0].toLowerCase())) {
                completions.add("resetbook");
            }
        }

        return completions;
    }
}