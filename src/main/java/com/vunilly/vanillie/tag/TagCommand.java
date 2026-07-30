package com.vunilly.vanillie.tag;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.tag.menu.TagMenu;
import com.vunilly.vanillie.utils.Lang;

public class TagCommand implements CommandExecutor {

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

        if (args.length >= 1) {
            sender.sendMessage(Lang.get("msg.tooManyParams").getFirst());
            return true;
        }

        TagMenu tagMenu = new TagMenu(player);
        player.openInventory(tagMenu.getInventory());

        return true;
    }
    
}
