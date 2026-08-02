package com.vunilly.vanillie.size;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.pvp.PvpManager;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class SizeCommand implements CommandExecutor {

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

        if (args.length > 0) {
            player.sendMessage(Lang.get("msg.tooManyParamsOkay", Placeholder.parsed("command", command.getName())).getFirst());
        }

        if (PvpManager.isInCombat(player.getUniqueId())) {
            player.sendMessage(Lang.get("msg.size.cantChange").getFirst());
            return true;
        }

        SizeMenu menu = new SizeMenu(player);
        player.openInventory(menu.getInventory());

        return true;
    }
}