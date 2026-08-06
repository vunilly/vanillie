package com.vunilly.vanillie.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.twitch.TwitchManager;

import net.kyori.adventure.text.Component;

public class NametagListener implements Listener {
    private static NametagListener instance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateNametag(player);
    }

    public static void updateNametag(Player player) {
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        if (TagManager.getAllActiveTagsForPlayer(player.getUniqueId()).size() >= 0) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            
            String teamName = "vanillie_tag_" + player.getUniqueId().toString().substring(0, 8);
            Team team = board.getTeam(teamName);
            if (team == null) {
                team = board.registerNewTeam(teamName);
            }

            boolean isLive = TwitchManager.isPlayerLiveOnTwitch(player.getUniqueId());

            Component suffix;
            if (isLive) {
                suffix = Vanillie.minimessage.deserialize(" <c:#ff0000>⏺</c> <b><gradient:#5146FF:#9146FF>[LIVE]</gradient></b>");
            } else {
                suffix = Component.empty();
            }

            team.addPlayer(player);
            if (TagManager.getAllActiveTagsForPlayer(player.getUniqueId()).size() == 0) {
                team.prefix(tags);
            } else {
                team.prefix(tags.append(Component.space()));
            }
            team.suffix(suffix);
        }
    }
}