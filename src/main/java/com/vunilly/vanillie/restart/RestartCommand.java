package com.vunilly.vanillie.restart;

import com.vunilly.vanillie.utils.Lang;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

public class RestartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args) {

        if (!sender.isOp()) {
            sender.sendMessage(Lang.get("msg.needOp").getFirst());
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Lang.get("msg.tooFewParams").getFirst());
            return true;
        }

        String message = String.join(" ", args);

        RestartManager.startRestart(message);

        return true;
    }
}
