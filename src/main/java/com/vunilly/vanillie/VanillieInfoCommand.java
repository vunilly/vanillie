package com.vunilly.vanillie;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import com.vunilly.vanillie.utils.Lang;

public class VanillieInfoCommand implements CommandExecutor {

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

                openInfoBook(player);

                return true;
        }

        private static void openInfoBook(Player player) {
                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta meta = (BookMeta) book.getItemMeta();

                meta.setTitle("Vanillie Info");
                meta.setAuthor("Server");

                meta.addPages(
                                Vanillie.minimessage.deserialize("""
                                                <b>Vanillie Plugin</b>
                                                Version: v1.5
                                                <b><i>For LUXSMP</i></b>
                                                Developed by vunilly

                                                twitch.tv/vunilly
                                                github.com/vunilly

                                                License: MIT
                                                """));

                book.setItemMeta(meta);
                player.openBook(book);
        }
}
