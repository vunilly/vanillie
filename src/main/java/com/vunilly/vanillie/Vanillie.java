package com.vunilly.vanillie;

import com.vunilly.vanillie.enderman.EndermanListener;
import com.vunilly.vanillie.restart.RestartCommand;
import com.vunilly.vanillie.settings.SettingsCommand;
import com.vunilly.vanillie.settings.SettingsManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.vunilly.vanillie.display.JoinLeaveListener;
import com.vunilly.vanillie.biome.BiomeWand;
import com.vunilly.vanillie.biome.BiomeWandListener;
import com.vunilly.vanillie.biome.RecipeListener;
import com.vunilly.vanillie.display.ChatListener;
import com.vunilly.vanillie.pvp.PvpCommand;
import com.vunilly.vanillie.pvp.PvpListener;
import com.vunilly.vanillie.pvp.PvpManager;
import com.vunilly.vanillie.size.SizeCommand;
import com.vunilly.vanillie.tag.TagCommand;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.twitch.TwitchCommand;
import com.vunilly.vanillie.twitch.TwitchManager;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.SignUi;
import com.vunilly.vanillie.vote.VoteCommand;

import net.kyori.adventure.text.minimessage.MiniMessage;

import com.vunilly.vanillie.utils.ClickMenuListener;

public class Vanillie extends JavaPlugin {
    private SignUi signUI;
    public final static MiniMessage minimessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        getLogger().info("Vanillie Plugin by vunilly enabled!");

        Lang.init(this);
        Lang.loadLang();
        this.signUI = new SignUi(this);


        getServer().getPluginManager().registerEvents(new ClickMenuListener(), this);

        getServer().getPluginManager().registerEvents(new EndermanListener(), this);
        
        TagManager.init(getDataFolder());
        TagManager.loadData();
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        if (getCommand("tag") != null) {
            getCommand("tag").setExecutor(new TagCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /tag command!");
        }
        
        PvpManager.init(getDataFolder());
        PvpManager.loadData();
        PvpManager.startTimerTask();
        if (getCommand("pvp") != null) {
            getCommand("pvp").setExecutor(new PvpCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /pvp command!");
        }
        getServer().getPluginManager().registerEvents(new PvpListener(), this);


        TwitchManager.init(getDataFolder());
        TwitchManager.loadData();
        if (getCommand("twitch") != null) {
            getCommand("twitch").setExecutor(new TwitchCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /twitch command!");
        }


        if (getCommand("vote") != null) {
            getCommand("vote").setExecutor(new VoteCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /vote command!");
        }


        if (getCommand("size") != null) {
            getCommand("size").setExecutor(new SizeCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /size command!");
        }

        BiomeWand biomeWand = new BiomeWand();
        Bukkit.addRecipe(biomeWand.getRecipe(this));
        getServer().getPluginManager().registerEvents(new RecipeListener(), this);
        getServer().getPluginManager().registerEvents(new BiomeWandListener(new BiomeWand()), this);


        if (getCommand("vanillieconf") != null) {
            getCommand("vanillieconf").setExecutor(new VanillieCommand());
            getCommand("vanillieconf").setTabCompleter(new VanillieCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /vanillieconf command!");
        }

        if (getCommand("vanillie") != null) {
            getCommand("vanillie").setExecutor(new VanillieInfoCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /vanillie command!");
        }

        if (getCommand("schedulerestart") != null) {
            getCommand("schedulerestart").setExecutor(new RestartCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /schedulerestart command!");
        }

        SettingsManager.init(getDataFolder());
        SettingsManager.loadData();
        if (getCommand("vanilliesettings") != null) {
            getCommand("vanilliesettings").setExecutor(new SettingsCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /vanilliesettings command!");
        }
    }

    public SignUi getSignUI() {
        return signUI;
    }

    @Override
    public void onDisable() {
        getLogger().info("Vanillie Plugin by vunilly disabled!");

        TagManager.saveData();
        Lang.saveLang();
    }
}