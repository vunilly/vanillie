package com.vunilly.vanillie.biome;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.vunilly.vanillie.Vanillie;

public class RecipeListener implements Listener {

    private final NamespacedKey recipeKey;

    public RecipeListener() {
        Vanillie plugin = (Vanillie) org.bukkit.plugin.java.JavaPlugin.getPlugin(Vanillie.class);
        this.recipeKey = new NamespacedKey(plugin, "vanillie_biomewand");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipe(this.recipeKey);
    }
}