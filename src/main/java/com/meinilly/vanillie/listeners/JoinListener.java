package com.meinilly.vanillie.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.meinilly.vanillie.Vanillie;
import com.meinilly.vanillie.commands.tag.TagManager;

import org.bukkit.event.Listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Component never_used_message = MiniMessage.miniMessage().deserialize(
                Vanillie.getPluginTitle(" ") + Vanillie.getGradientText(
                        "Du hast keine aktiven Tags."));

        Component used_before_message = MiniMessage.miniMessage().deserialize(
                Vanillie.getPluginTitle(" ") + Vanillie.getGradientText(
                        "Aktive Tags: " + TagManager.getTag(player.getUniqueId())));
        
        List<Integer> playerTags = TagManager.getPlayerUsedTags().get(player.getUniqueId());
        if (playerTags == null || playerTags.size() == 0) {
            player.sendMessage(never_used_message);
        } else {
            player.sendMessage(used_before_message);
        }

        
    }
}