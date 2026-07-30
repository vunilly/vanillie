package com.vunilly.vanillie;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.utils.Lang;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.get("msg.onlyPlayersAllowed").getFirst());
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(Lang.get("msg.needOp").getFirst());
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cBenutzung: /" + label + " <reload|save>");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload":
                TagManager.clearData();
                TagManager.loadData();

                Lang.clearData();
                Lang.loadLang();

                sender.sendMessage(Lang.get("msg.confReloaded").getFirst());
                break;

            case "save":
                TagManager.saveData();
                sender.sendMessage(Lang.get("msg.confSaved").getFirst());
                break;

            default:
                sender.sendMessage("§cUnbekannter Parameter.");
                sender.sendMessage("§7Verwende: /" + label + " <reload|save>");
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
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }

            if ("save".startsWith(args[0].toLowerCase())) {
                completions.add("save");
            }
        }

        return completions;
    }
}