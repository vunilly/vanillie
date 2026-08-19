package com.vunilly.vanillie.restart;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.utils.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RestartManager {

    private static BossBar restartBossBar = null;
    private static int bossBarUpdateTaskId = -1;

    private static final int VOTING_DURATION = 20 * 60; // 60 seconds = 1200 ticks

    public static void startRestart(String reason) {

        // Restart findet in 30 Sekunden statt
        String time = LocalTime.now()
                .plusSeconds(30)
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Component voteMessage = Lang.get(
                "msg.restart.scheduled",
                Placeholder.parsed("reason", reason),
                Placeholder.parsed("time", time)
        ).getFirst();

        Bukkit.getOnlinePlayers().forEach(player ->
                player.sendMessage(voteMessage)
        );

        restartBossBar = Bukkit.createBossBar(
                PlainTextComponentSerializer.plainText()
                        .serialize(Lang.get("msg.restart.bossbar").getFirst()),
                BarColor.YELLOW,
                BarStyle.SOLID
        );

        restartBossBar.setProgress(1.0);

        Bukkit.getOnlinePlayers().forEach(restartBossBar::addPlayer);

        restartStartTime = System.currentTimeMillis();

        startRestartTimer();
        startBossBarUpdateTask();
    }

    private static long restartStartTime = 0;

    private static void startBossBarUpdateTask() {
        bossBarUpdateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class),
                RestartManager::updateBossBar,
                0L,
                5L
        );
    }

    private static void updateBossBar() {
        if (restartBossBar == null) {
            return;
        }

        long elapsedTicks =
                (System.currentTimeMillis() - restartStartTime) / 50;

        double progress = Math.max(
                0.0,
                1.0 - (double) elapsedTicks / VOTING_DURATION
        );

        restartBossBar.setProgress(progress);
    }

    private static void startRestartTimer() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class),
                RestartManager::endRestartCountdown,
                VOTING_DURATION
        );
    }

    private static void endRestartCountdown() {

        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin
                .getPlugin(Vanillie.class);

        Bukkit.getOnlinePlayers().forEach(player ->
                player.sendMessage(Lang.get("msg.restart.starting").getFirst())
        );

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            Bukkit.getOnlinePlayers().forEach(player ->
                    player.sendMessage(Lang.get("msg.restart.saving").getFirst())
            );

            Bukkit.getServer().savePlayers();

            for (World world : Bukkit.getWorlds()) {
                world.save();
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                Bukkit.getOnlinePlayers().forEach(player ->
                        player.sendMessage(Lang.get("msg.restart.bye").getFirst())
                );

                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                    Component message =
                            Lang.get("msg.restart.kicked").getFirst();

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kick(message);
                    }

                    Bukkit.getServer().shutdown();

                }, 20L);

            }, 20L);

        }, 20L);
    }
}