package com.meinilly.vanillie;

import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import com.meinilly.vanillie.commands.tag.TagCommand;
import com.meinilly.vanillie.commands.tag.TagManager;
import com.meinilly.vanillie.listeners.ChatListener;
import com.meinilly.vanillie.listeners.JoinListener;
import com.meinilly.vanillie.tagmenu.TagMenuUi;
import com.meinilly.vanillie.tagmenu.TagUiCmd;
import com.meinilly.vanillie.tagmenu.TagUiListener;

public class Vanillie extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Vanillie Plugin by meinilly enabled!");

        TagManager.init(getDataFolder());
        TagManager.loadTags();

        // getServer().getPluginManager().registerEvents(new JoinListener(), this);
        // getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new TagUiListener(), this);
        //getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

        // Old Tag
        // if (getCommand("tag") != null) {
        //     getCommand("tag").setExecutor(new TagCommand());
        //     getCommand("tag").setTabCompleter(new TagCommand());
        // } else {
        //     getLogger().severe("Vanillie failed to register the /tag command!");
        // }

        if (getCommand("tag") != null) {
            getCommand("tag").setExecutor(new TagUiCmd());
        } else {
            getLogger().severe("Vanillie failed to register the /tag command!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Vanillie Plugin by vunilly disabled!");
        
        TagManager.saveTags(getDataFolder());
    }

    public static String getGradientText(String text) {
        return "<gradient:#7f6bff:#c4abff>" + text + "</gradient>";
    }

    public static String getImportantText(String text) {
        return "<gradient:#ff52dc:#c4abff>" + text + "</gradient>";
    }

    public static String getPluginTitle(String text) {
        return "<b><gradient:#ff90ff:#5e5ef7>[Vanillie <gradient:#aa00ff:#FFC21F>LUX SMP</gradient>] " + text + "</gradient></b>";
    }
}