package com.vunilly.vanillie.settings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.pvp.PvpMenu;
import com.vunilly.vanillie.utils.Lang;

public class SettingsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("msg.onlyPlayersAllowed").getFirst());
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(Lang.get("msg.needOp").getFirst());
            return true;
        }

        SettingsMenu menu = new SettingsMenu(player);
        player.openInventory(menu.getInventory());

        return true;
    }
    
}
