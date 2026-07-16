package com.meinilly.vanillie.tagmenu;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.Command;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagUiCmd implements CommandExecutor {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command,
            @NotNull String label, @NotNull String @NotNull [] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen!");
            return true;
        }
        
        TagMenuUi ui = new TagMenuUi();
        player.openInventory(ui.getInventory());
        return true;
    }
}
