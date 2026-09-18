package com.vunilly.vanillie.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.twitch.TwitchManager;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class JoinLeaveListener implements Listener {
    
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

        NametagListener.updateNametag(player);

        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean isLive = TwitchManager.isPlayerLiveOnTwitch(player.getUniqueId());

            if (isLive) {
                Bukkit.broadcast(Lang
                        .get("msg.twitch.isLive",
                                Placeholder.parsed("twitchname", TwitchManager.getTwitchUsername(player.getUniqueId())),
                                Placeholder.parsed("player", player.getName()))
                        .getFirst());
            }
        });

        event.joinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        TwitchManager.invalidateCache(player.getUniqueId());

        Component quitMessage = Component.text()
                .append(tags)
                .append(Component.space())
                .append(Component.text(player.getName()))
                .append(Lang.get("msg.playerLeft").getFirst())
                .build();

        event.quitMessage(quitMessage);
    }
}