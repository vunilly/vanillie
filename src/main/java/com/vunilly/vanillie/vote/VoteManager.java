package com.vunilly.vanillie.vote;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class VoteManager {
    private static int currentWishType = -1; // -1 = none, 0 = clear, 1 = night, 2 = day
    private static UUID voteInitiator = null;
    private static Set<UUID> yesVotes = new HashSet<>();
    private static Set<UUID> noVotes = new HashSet<>();
    private static int votingTaskId = -1;
    private static int bossBarUpdateTaskId = -1;
    private static long votingStartTime = 0;
    private static BossBar votingBossBar = null;
    private static final int VOTING_DURATION = 240; // 12 seconds in ticks
    private static final double VOTE_THRESHOLD = 0.49; // 49% need to vote yes

    public static boolean startWish(int voteType, UUID initiator) {
        if (currentWishType != -1) {
            Player player = Bukkit.getPlayer(initiator);
            if (player != null) {
                player.sendMessage(Lang.get("msg.vote.alreadyRunning").getFirst());
            }
            return false;
        }

        currentWishType = voteType;
        voteInitiator = initiator;
        yesVotes.clear();
        noVotes.clear();
        votingStartTime = System.currentTimeMillis();

        // Broadcast voting message
        Component voteMessage = getVoteMessage(voteType, initiator);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(voteMessage));

        // Create and show BossBar
        createBossBar(voteType);

        // Start voting task
        startVotingTimer();
        startBossBarUpdateTask();
        return true;
    }

    private static void createBossBar(int voteType) {
        BarColor barColor = switch (voteType) {
            case 0 -> BarColor.WHITE; // Clear weather
            case 1 -> BarColor.PURPLE; // Night
            case 2 -> BarColor.YELLOW; // Day
            case 3 -> BarColor.BLUE; // Rain
            case 4 -> BarColor.BLUE; // Thunder
            default -> BarColor.WHITE;
        };

        Component barTitle = switch (voteType) {
            case 0 -> Lang.get("msg.vote.bossbar.clear").getFirst();
            case 1 -> Lang.get("msg.vote.bossbar.night").getFirst();
            case 2 -> Lang.get("msg.vote.bossbar.day").getFirst();
            case 3 -> Lang.get("msg.vote.bossbar.rain").getFirst();
            case 4 -> Lang.get("msg.vote.bossbar.thunder").getFirst();
            default -> Component.empty();
        };

        votingBossBar = Bukkit.createBossBar(PlainTextComponentSerializer.plainText().serialize(barTitle), barColor,
                BarStyle.SOLID);
        votingBossBar.setProgress(1.0);

        Bukkit.getOnlinePlayers().forEach(votingBossBar::addPlayer);
    }

    private static void startBossBarUpdateTask() {
        bossBarUpdateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class),
                VoteManager::updateBossBar,
                0,
                5 // Update every 5 ticks (0.25 seconds)
        );
    }

    private static void updateBossBar() {
        if (votingBossBar == null || currentWishType == -1) {
            return;
        }

        long elapsedTicks = (System.currentTimeMillis() - votingStartTime) / 50; // Convert ms to ticks
        double progress = Math.max(0.0, 1.0 - (double) elapsedTicks / VOTING_DURATION);
        votingBossBar.setProgress(progress);
    }

    private static void startVotingTimer() {
        votingTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(
                (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class),
                VoteManager::endVoting,
                VOTING_DURATION);
    }

    public static boolean addVote(UUID voter, boolean voteYes) {
        if (currentWishType == -1) {
            Player player = Bukkit.getPlayer(voter);
            if (player != null) {
                player.sendMessage(Lang.get("msg.vote.nonRunning").getFirst());
            }
            return false;
        }

        if (yesVotes.contains(voter) || noVotes.contains(voter)) {
            Player player = Bukkit.getPlayer(voter);
            if (player != null) {
                player.sendMessage(Lang.get("msg.vote.voted.already").getFirst());
            }
            return false;
        }

        if (voteYes) {
            yesVotes.add(voter);
            Player player = Bukkit.getPlayer(voter);
            if (player != null) {
                player.sendMessage(Lang.get("msg.vote.voted.yes").getFirst());
            }
        } else {
            noVotes.add(voter);
            Player player = Bukkit.getPlayer(voter);
            if (player != null) {
                player.sendMessage(Lang.get("msg.vote.voted.no").getFirst());
            }
        }

        return true;
    }

    private static void endVoting() {
        if (currentWishType == -1) {
            return;
        }

        int onlinePlayersExceptInitiator = Bukkit.getOnlinePlayers().size() - 1;

        boolean granted = false;
        if (onlinePlayersExceptInitiator > 0) {
            double yesPercentage = (double) yesVotes.size() / onlinePlayersExceptInitiator;
            granted = yesPercentage >= VOTE_THRESHOLD;
        } else if (onlinePlayersExceptInitiator == 0) {
            granted = true;
        }

        Component resultMessage;
        if (granted) {
            resultMessage = getGrantedMessage(currentWishType);
            executeWish(currentWishType);
        } else {
            resultMessage = getNotGrantedMessage(currentWishType);
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(resultMessage));

        resetVoting();
    }

    private static void executeWish(int voteType) {
        World world = Bukkit.getWorlds().get(0);
        if (world == null)
            return;

        switch (voteType) {
            case 0: // Clear weather
                world.setStorm(false);
                world.setThundering(false);
                break;
            case 1: // Night
                world.setTime(13000);
                break;
            case 2: // Day
                world.setTime(0);
                break;
            case 3: // Rain
                world.setStorm(true);
                world.setThundering(false);
                break;
            case 4: // Thunder
                world.setStorm(true);
                world.setThundering(true);
                break;
        }
    }

    private static Component getVoteMessage(int voteType, UUID initiatorUUID) {
        Player initiator = Bukkit.getPlayer(initiatorUUID);
        String playerName = initiator != null ? initiator.getName() : "Unknown";

        String langKey = switch (voteType) {
            case 0 -> "msg.vote.type.clear";
            case 1 -> "msg.vote.type.night";
            case 2 -> "msg.vote.type.day";
            case 3 -> "msg.vote.type.rain";
            case 4 -> "msg.vote.type.thunder";
            default -> "";
        };

        // Build clickable components for yes and no
        Component yesButton = Component.text(Lang.getString("str.clickHereYes"))
                .clickEvent(ClickEvent.runCommand("/vote yes"));

        Component noButton = Component.text(Lang.getString("str.clickHereNo"))
                .clickEvent(ClickEvent.runCommand("/vote no"));

        // Use component placeholders for clickable elements
        Component message = Lang.get(langKey,
                Placeholder.component("player", Component.text(playerName)),
                Placeholder.component("yes", yesButton),
                Placeholder.component("no", noButton)).getFirst();

        return message;
    }

    private static Component getGrantedMessage(int voteType) {
        String langKey = switch (voteType) {
            case 0 -> "msg.vote.granted.clear";
            case 1 -> "msg.vote.granted.night";
            case 2 -> "msg.vote.granted.day";
            case 3 -> "msg.vote.granted.rain";
            case 4 -> "msg.vote.granted.thunder";
            default -> "";
        };

        return Lang.get(langKey).getFirst();
    }

    private static Component getNotGrantedMessage(int voteType) {
        String langKey = switch (voteType) {
            case 0 -> "msg.vote.notGranted.clear";
            case 1 -> "msg.vote.notGranted.night";
            case 2 -> "msg.vote.notGranted.day";
            case 3 -> "msg.vote.notGranted.rain";
            case 4 -> "msg.vote.notGranted.thunder";
            default -> "";
        };

        return Lang.get(langKey).getFirst();
    }

    private static void resetVoting() {
        currentWishType = -1;
        voteInitiator = null;
        yesVotes.clear();
        noVotes.clear();

        if (votingTaskId != -1) {
            Bukkit.getScheduler().cancelTask(votingTaskId);
            votingTaskId = -1;
        }

        if (bossBarUpdateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(bossBarUpdateTaskId);
            bossBarUpdateTaskId = -1;
        }

        if (votingBossBar != null) {
            votingBossBar.removeAll();
            votingBossBar = null;
        }

        votingStartTime = 0;
    }

    public static boolean isVotingActive() {
        return currentWishType != -1;
    }

    public static int getCurrentWishType() {
        return currentWishType;
    }
}