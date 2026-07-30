package com.vunilly.vanillie.tag.display;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TagJoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        Component joinMessage = Component.text()
                .append(tags)
                .append(Component.space())
                .append(Component.text(player.getName()))
                .append(Lang.get("msg.playerJoined").getFirst())
                .build();

        event.joinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        Component quitMessage = Component.text()
                .append(tags)
                .append(Component.space())
                .append(Component.text(player.getName()))
                .append(Lang.get("msg.playerLeft").getFirst())
                .build();

        event.quitMessage(quitMessage);
    }
}