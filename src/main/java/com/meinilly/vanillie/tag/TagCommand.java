package com.meinilly.vanillie.tag;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.tag.menu.TagMenu;

public class TagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
            return true;
        }

        if (args.length >= 1) {
            sender.sendMessage("Dieser Befehl benötigt keine weiteren Angaben.");
            return true;
        }

        TagMenu tagMenu = new TagMenu(player);
        player.openInventory(tagMenu.getInventory());

        return true;
    }
    
}
