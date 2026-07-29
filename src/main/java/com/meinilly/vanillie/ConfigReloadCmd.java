package com.meinilly.vanillie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.tag.TagManager;
import com.meinilly.vanillie.tag.menu.TagMenu;
import com.meinilly.vanillie.utils.Lang;

public class ConfigReloadCmd implements CommandExecutor {
    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.getString("msg.onlyPlayersAllowed"));
            return true;
        }
        
        if (!(sender.isOp())) {
            sender.sendMessage(Lang.getString("msg.needOp"));
            return true;
        }

        if (args.length >= 1) {
            sender.sendMessage(Lang.getString("msg.tooManyParams"));
            return true;
        }

        TagManager.clearData();
        TagManager.loadData();
        Lang.clearData();
        Lang.loadLang();

        sender.sendMessage(Lang.getString("msg.confReloaded"));

        return true;
    }
    
}
