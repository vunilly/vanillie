package com.vunilly.vanillie.pvp;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class PvpCommand implements CommandExecutor {

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

        AttributeInstance scale = player.getAttribute(Attribute.SCALE);
        if (scale.getBaseValue() <= 0.75) {
            player.sendMessage(Lang.get("msg.pvp.tooLow.you").getFirst());
            return true;
        }

        PvpMenu menu = new PvpMenu(player);
        player.openInventory(menu.getInventory());

        return true;
    }
}