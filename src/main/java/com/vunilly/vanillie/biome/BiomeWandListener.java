package com.vunilly.vanillie.biome;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.biome.BiomeWand;
import com.vunilly.vanillie.utils.Utils;

import io.papermc.paper.datacomponent.DataComponentTypes;

public class BiomeWandListener implements Listener {
    private static BiomeWand biomeWand;
    private final int COOLDOWN_TICKS = 15;

    public BiomeWandListener(BiomeWand biomeWand) {
        BiomeWandListener.biomeWand = biomeWand;

        startParticles();
    }

    @EventHandler 
    public void onPLayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item == null || !biomeWand.isWand(item)) return;

        player.getWorld().spawnParticle(Particle.ASH, player.getLocation(), 67);
    }

    private void startParticles() {
        Vanillie plugin = JavaPlugin.getPlugin(Vanillie.class);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item == null || !biomeWand.isWand(item)) return;

                Location location = Utils.getHandLocation(player, true);

                double offset = 0.05;

                Color leafColor = Color.fromRGB(55, 190, 55);

                player.getWorld().spawnParticle(
                    Particle.TINTED_LEAVES,
                    location,
                    5,
                    offset, offset, offset,
                    0.06,
                    leafColor
                );
            }
        }, 0L, 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();

        if (item == null || !biomeWand.isWand(item)) return;

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        if (player.hasCooldown(item.getType())) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        player.setCooldown(item.getType(), COOLDOWN_TICKS);

        if (player.getGameMode() != GameMode.CREATIVE) {
            applyDamage(player, item, 1);
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.0f);
        }
    }

    private void applyDamage(Player player, ItemStack item, int amount) {
        Integer maxDamage = item.getData(DataComponentTypes.MAX_DAMAGE);
        Integer currentDamage = item.getData(DataComponentTypes.DAMAGE);

        if (maxDamage == null) maxDamage = 512;
        if (currentDamage == null) currentDamage = 0;

        int newDamage = currentDamage + amount;

        if (newDamage >= maxDamage) {
            item.setAmount(0);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        } else {
            item.setData(DataComponentTypes.DAMAGE, newDamage);
        }
    }
}
