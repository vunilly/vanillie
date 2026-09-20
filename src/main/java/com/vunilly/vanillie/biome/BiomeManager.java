package com.vunilly.vanillie.biome;

import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeManager {
    private static final Map<UUID, Integer> playerSelectedBiomes = new ConcurrentHashMap<>();
    public static Map<String, Material> biomes = new LinkedHashMap<>();

    static {
        biomes.put("plains", Material.SHORT_GRASS);
        biomes.put("savanna", Material.SHORT_DRY_GRASS);
        biomes.put("forest", Material.GRASS_BLOCK);
        biomes.put("taiga", Material.PODZOL);
        biomes.put("jungle", Material.BAMBOO);
        biomes.put("swamp", Material.LILY_PAD);
        biomes.put("birch_forest", Material.BIRCH_LOG);
        biomes.put("dark_forest", Material.RED_MUSHROOM_BLOCK);
        biomes.put("badlands", Material.COARSE_DIRT);
        biomes.put("mushroom_fields", Material.MYCELIUM);
        biomes.put("cherry_grove", Material.CHERRY_LEAVES);
        biomes.put("pale_garden", Material.PALE_OAK_LEAVES);
        biomes.put("ocean", Material.SEA_PICKLE);
        biomes.put("warm_ocean", Material.TUBE_CORAL_BLOCK);
    }

    public static void setBiome(UUID uuid, int biomeId) {
        playerSelectedBiomes.put(uuid, biomeId);
    }

    public static int getBiomeId(UUID uuid) {
        return playerSelectedBiomes.getOrDefault(uuid, 0);
    }

    public static String getBiomeName(UUID uuid) {
        int id = getBiomeId(uuid);
        return getBiomeById(id);
    }

    public static String getBiomeById(int id) {
        return biomes.keySet().stream()
                .skip(id)
                .findFirst()
                .orElse(null);
    }
}
