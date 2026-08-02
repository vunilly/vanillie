package com.vunilly.vanillie.vote;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class VoteCommand implements CommandExecutor {

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

        if (args.length > 1) {
            player.sendMessage(Lang.get("msg.tooManyParamsOkay", Placeholder.parsed("command", command.getName())).getFirst());
        }

        if (args.length == 0) {
            VoteMenu menu = new VoteMenu(player);
            player.openInventory(menu.getInventory());
        } else {
            switch (args[0].toLowerCase()) {

                case "yes":
                    VoteManager.addVote(player.getUniqueId(), true);
                    break;

                case "no":
                    VoteManager.addVote(player.getUniqueId(), false);
                    break;

                default:
                    player.sendMessage(Lang.get("msg.incorrectParams").getFirst());
                    return true;
            }

            return true;
        }
        return true;

    }
}