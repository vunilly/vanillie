package com.vunilly.vanillie.biome;

import com.destroystokyo.paper.ParticleBuilder;
import com.vunilly.vanillie.pvp.PvpMenu;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.biome.BiomeWand;
import com.vunilly.vanillie.utils.Utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class BiomeWandListener implements Listener {
    private static BiomeWand biomeWand;
    private final int COOLDOWN_TICKS = 15;

    public BiomeWandListener(BiomeWand biomeWand) {
        BiomeWandListener.biomeWand = biomeWand;

        startParticles();
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();

        ItemStack wand = anvil.getFirstItem();
        ItemStack repairItem = anvil.getSecondItem();

        if (wand == null || !biomeWand.isWand(wand)) return;
        if (repairItem == null || repairItem.getType() != Material.AMETHYST_SHARD) return;

        ItemStack result = wand.clone();

        Integer damage = result.getData(DataComponentTypes.DAMAGE);
        if (damage == null) {
            damage = 0;
        }

        result.setData(
                DataComponentTypes.DAMAGE,
                Math.max(0, damage - 180)
        );

        event.setResult(result);
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory anvil)) return;
        if (event.getRawSlot() != 2) return;

        ItemStack result = event.getCurrentItem();

        if (result == null || result.getType().isAir()) return;

        ItemStack wand = anvil.getFirstItem();
        ItemStack repairItem = anvil.getSecondItem();

        if (wand == null || !biomeWand.isWand(wand)) return;
        if (repairItem == null || repairItem.getType() != Material.AMETHYST_SHARD) return;

        event.setCancelled(true);

        var player = (Player) event.getWhoClicked();
        var cursor = player.getItemOnCursor();

        if (cursor.getType().isAir()) {
            player.setItemOnCursor(result.clone());
        } else {
            player.getInventory().addItem(result.clone());
        }

        anvil.setFirstItem(null);
        repairItem.setAmount(repairItem.getAmount() - 1);
        if (repairItem.getAmount() <= 0) {
            anvil.setSecondItem(null);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (item != null && biomeWand.isWand(item)) {
            item.setData(DataComponentTypes.MAX_DAMAGE, 1024);
            item.setData(DataComponentTypes.DAMAGE, 0);
        }
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

                player.getWorld().spawnParticle(
                        Particle.END_ROD,
                        location,
                        3,
                        offset, offset, offset,
                        0.06
                );
            }
        }, 0L, 5L);
    }

    private void playAnimation(Player player) {
        Vanillie plugin = JavaPlugin.getPlugin(Vanillie.class);

        new BukkitRunnable() {
            int ticks = 0;

            final int max_ticks = 20;

            @Override
            public void run() {
                if (ticks++ >= max_ticks) { // 20 ticks = 1 second
                    changeBiome(player.getWorld(), player.getLocation(), BiomeManager.getBiomeName(player.getUniqueId()));
                    Bukkit.getLogger().info("Changing at: "+player.getLocation()+" Name: "+BiomeManager.getBiomeName(player.getUniqueId()));
                    cancel();
                    return;
                }

                float radius = (float) (ticks * 0.5);
                float angle = (float) (((double) ticks / max_ticks) * Math.PI * 2);
                float x = (float) (Math.cos(angle) * radius);
                float z = (float) (Math.sin(angle) * radius);
                float y = (float) ((float) max_ticks*0.5 - (ticks * 0.5));

                player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        player.getLocation().add(x, y, z),
                        30,
                        0.1, 0.1, 0.1,
                        0.06
                );

                x = (float) (Math.cos(angle + (Math.PI * 2) / 3) * radius);
                z = (float) (Math.sin(angle + (Math.PI * 2) / 3) * radius);

                player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        player.getLocation().add(x, y, z),
                        30,
                        0.1, 0.1, 0.1,
                        0.06
                );

                x = (float) (Math.cos(angle + ((Math.PI * 2) / 3)*2) * radius);
                z = (float) (Math.sin(angle + ((Math.PI * 2) / 3)*2) * radius);

                player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        player.getLocation().add(x, y, z),
                        30,
                        0.1, 0.1, 0.1,
                        0.06
                );


                Particle particle = new ParticleBuilder(Particle.DUST)
                        .location(player.getLocation())
                        .count(30)
                        .offset(5.0, 5.0, 5.0)
                        .extra(0.6)
                        .data(new Particle.DustOptions(Color.fromRGB(189, 156, 255), 2.0f))
                        .spawn().particle();

                if (ticks % 5 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0f, 0.8f + (ticks * 0.4f));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();

        if (item == null || !biomeWand.isWand(item)) return;

        Player player = event.getPlayer();

        BiomeWandMenu menu = new BiomeWandMenu(player.getUniqueId());
        player.openInventory(menu.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();

        if (item == null || !biomeWand.isWand(item)) return;

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
            playAnimation(player);
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

    private void changeBiome(World world, Location center, String biomeName) {
        try {
            NamespacedKey key = NamespacedKey.minecraft(biomeName.toLowerCase());
            Registry<Biome> biomeRegistry = Bukkit.getRegistry(Biome.class);
            Biome biome = biomeRegistry.get(key);

            if (biome == null) {
                throw new IllegalArgumentException("Biom nicht gefunden: " + biomeName);
            }

            int radius = 12;
            Set<Long> updatedChunks = new HashSet<>();

            for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
                for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
                    long chunkKey = Chunk.getChunkKey(x >> 4, z >> 4);
                    updatedChunks.add(chunkKey);

                    for (int y = center.getBlockY() - 10; y <= center.getBlockY() + 10; y++) {
                        world.setBiome(x, y, z, biome);
                    }
                }
            }

            for (long chunkKey : updatedChunks) {
                Chunk chunk = world.getChunkAt(chunkKey);
                world.refreshChunk(chunk.getX(), chunk.getZ());
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("Fehler beim ändern des Bioms: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
