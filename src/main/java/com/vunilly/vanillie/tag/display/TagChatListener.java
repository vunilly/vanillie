package com.vunilly.vanillie.tag.display;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vunilly.vanillie.tag.TagManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class TagChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        Component prefix;
        if (TagManager.getAllActiveTagsForPlayer(player.getUniqueId()).size() > 0) {
            prefix = tags.append(Component.space());

            event.renderer((source, sourceDisplayName, message, viewer) -> {
                return Component.text()
                        .append(prefix)
                        .append(sourceDisplayName)
                        .append(Component.text(": "))
                        .append(message)
                        .build();
            });
        }
    }
}