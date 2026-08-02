package com.vunilly.vanillie.tag.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.vunilly.vanillie.tag.TagManager;

import net.kyori.adventure.text.Component;

public class TagNametagListener implements Listener {
    private static TagNametagListener instance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateNametag(player);
    }

    public static void updateNametag(Player player) {
        Component tags = TagManager.getPlayerTags(player.getUniqueId());

        if (TagManager.getAllActiveTagsForPlayer(player.getUniqueId()).size() >= 0) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            
            // Eindeutiger Team-Name
            String teamName = "vanillie_tag_" + player.getUniqueId().toString().substring(0, 8);
            Team team = board.getTeam(teamName);
            
            if (team == null) {
                team = board.registerNewTeam(teamName);
            }
            
            team.addPlayer(player);
            if (TagManager.getAllActiveTagsForPlayer(player.getUniqueId()).size() == 0) {
                team.prefix(tags);
            } else {
                team.prefix(tags.append(Component.text(" ")));
            }
            
            team.suffix(Component.text(" "));
        }
    }
}