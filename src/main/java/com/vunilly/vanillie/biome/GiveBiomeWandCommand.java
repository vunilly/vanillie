package com.vunilly.vanillie.biome;

import com.vunilly.vanillie.utils.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveBiomeWandCommand implements CommandExecutor {
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

        if (!sender.isOp()) {
            sender.sendMessage(Lang.get("msg.needOp").getFirst());
            return true;
        }

        ItemStack biomeWand = new BiomeWand().createItem();

        player.getInventory().addItem(biomeWand);
        return true;
    }
}
