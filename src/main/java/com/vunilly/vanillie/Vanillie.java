package com.vunilly.vanillie;

import org.bukkit.plugin.java.JavaPlugin;

import com.vunilly.vanillie.display.JoinLeaveListener;
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