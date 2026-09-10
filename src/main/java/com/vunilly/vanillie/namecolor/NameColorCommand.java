package com.vunilly.vanillie.namecolor;

import com.vunilly.vanillie.pvp.PvpMenu;
import com.vunilly.vanillie.utils.Lang;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameColorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("msg.onlyPlayersAllowed").getFirst());
            return true;
        }

        if (args.length > 0) {
            player.sendMessage(Lang.get("msg.tooManyParamsOkay", Placeholder.parsed("command", command.getName())).getFirst());
        }

        NameColorMenu menu = new NameColorMenu(player);
        player.openInventory(menu.getInventory());

        return true;
    }
}
