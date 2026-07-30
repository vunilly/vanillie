package com.vunilly.vanillie;

import org.bukkit.plugin.java.JavaPlugin;

import com.vunilly.vanillie.tag.TagCommand;
import com.vunilly.vanillie.tag.TagManager;
import com.vunilly.vanillie.tag.TagMenuListener;
import com.vunilly.vanillie.tag.display.TagChatListener;
import com.vunilly.vanillie.tag.display.TagJoinLeaveListener;
import com.vunilly.vanillie.utils.Lang;
import com.vunilly.vanillie.utils.SignUi;

public class Vanillie extends JavaPlugin {
    private SignUi signUI;

    @Override
    public void onEnable() {
        getLogger().info("Vanillie Plugin by vunilly enabled!");

        Lang.init(getDataFolder());
        Lang.loadLang();

        TagManager.init(getDataFolder());
        TagManager.loadData();

        this.signUI = new SignUi(this);

        getServer().getPluginManager().registerEvents(new TagMenuListener(), this);
        getServer().getPluginManager().registerEvents(new TagChatListener(), this);
        getServer().getPluginManager().registerEvents(new TagJoinLeaveListener(), this);

        if (getCommand("tag") != null) {
            getCommand("tag").setExecutor(new TagCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /tag command!");
        }

        if (getCommand("vanillieconfig") != null) {
            getCommand("vanillieconfig").setExecutor(new ConfigCommand());
            getCommand("vanillieconfig").setTabCompleter(new ConfigCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /vanillieconfig command!");
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