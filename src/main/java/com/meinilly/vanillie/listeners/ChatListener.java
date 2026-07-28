package com.meinilly.vanillie.listeners;

import org.bukkit.event.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import com.meinilly.vanillie.commands.oldtag.TagManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class ChatListener implements Listener {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String playersTagesRaw = TagManager.getTag(player.getUniqueId());

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            String format = "<tag> <player_name><gray>:</gray> <message>";

            TagResolver placeholders = TagResolver.resolver(
                Placeholder.parsed("tag", playersTagesRaw),
                Placeholder.component("player_name", sourceDisplayName),
                Placeholder.component("message", message)
            );

            return miniMessage.deserialize(format, placeholders);
        });
    }
}
