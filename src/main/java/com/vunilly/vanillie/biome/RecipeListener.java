package com.vunilly.vanillie.biome;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.vunilly.vanillie.Vanillie;

public class RecipeListener implements Listener {

    private final BiomeWand biomeWand;

    public RecipeListener(BiomeWand biomeWand) {
        this.biomeWand = biomeWand;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipe(biomeWand.getRecipeKey());
    }
}
