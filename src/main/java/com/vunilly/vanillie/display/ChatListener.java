package com.vunilly.vanillie.display;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.twitch.TwitchManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        if (TagManager.getAllActiveTagsForPlayer(player.getUniqueId()).size() > 0) {
            Component prefix = tags.append(Component.space());
            
            // Live-Status JETZT abrufen (gecacht, also schnell)
            boolean isLive = TwitchManager.isPlayerLiveOnTwitch(player.getUniqueId());
            
            final Component suffix;
            if (isLive) {
                suffix = Vanillie.minimessage.deserialize(" <c:#ff0000>⏺</c> <b><gradient:#5146FF:#9146FF>[LIVE]</gradient></b>");
            } else {
                suffix = Component.text(" ");
            }

            event.renderer((source, sourceDisplayName, message, viewer) -> {
                return Component.text()
                        .append(prefix)
                        .append(sourceDisplayName)
                        .append(suffix)
                        .append(Component.text(": "))
                        .append(message)
                        .build();
            });
        }
    }
}