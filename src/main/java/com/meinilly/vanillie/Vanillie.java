package com.meinilly.vanillie;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.meinilly.vanillie.commands.oldtag.TagCommandOld;
import com.meinilly.vanillie.commands.oldtag.TagManager;
import com.meinilly.vanillie.listeners.ChatListener;
import com.meinilly.vanillie.listeners.JoinListener;
import com.meinilly.vanillie.tag.TagCommand;
import com.meinilly.vanillie.tagmenu.TagMenuUi;
import com.meinilly.vanillie.tagmenu.TagUiCmd;
import com.meinilly.vanillie.utils.Lang;
import com.meinilly.vanillie.utils.SignUi;
import com.meinilly.vanillie.utils.Utils;
import com.meinilly.vanillie.tag.TagMenuListener;

public class Vanillie extends JavaPlugin {
    private SignUi signUI;

    @Override
    public void onEnable() {
        getLogger().info("Vanillie Plugin by meinilly enabled!");

        Lang.init(getDataFolder());
        Lang.loadLang();

        TagManager.init(getDataFolder());
        TagManager.loadTags();

        this.signUI = new SignUi(this);

        getServer().getPluginManager().registerEvents(new TagMenuListener(), this);

        if (getCommand("tag") != null) {
            getCommand("tag").setExecutor(new TagCommand());
        } else {
            getLogger().severe("Vanillie failed to register the /tag command!");
        }

        //this.getCommand("textinput").setExecutor(new TextinputCommand(this));
    }

    public SignUi getSignUI() {
        return signUI;
    }

    @Override
    public void onDisable() {
        getLogger().info("Vanillie Plugin by vunilly disabled!");

        TagManager.saveTags(getDataFolder());
        Lang.saveLang(getDataFolder());
    }

    public static String getGradientText(String text) {
        return "<gradient:#7f6bff:#c4abff>" + text + "</gradient>";
    }

    public static String getImportantText(String text) {
        return "<gradient:#ff52dc:#c4abff>" + text + "</gradient>";
    }

    public static String getGradientTextGreen(String text) {
        return "<gradient:#4ceb34:#34eb98>" + text + "</gradient>";
    }

    public static String getGradientTextSecondary(String text) {
        return "<gradient:#ff52dc:#a719ff>" + text + "</gradient>";
    }

    public static String getPluginTitle(String text) {
        return "<b><gradient:#ff90ff:#5e5ef7>[Vanillie <gradient:#aa00ff:#FFC21F>LUX SMP</gradient>] " + text
                + "</gradient></b>";
    }

    public static ItemStack createCustomHeadItem(String base64Texture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile("textures");
        profile.setProperty(new ProfileProperty("textures", base64Texture));

        skullMeta.setPlayerProfile(profile);

        head.setItemMeta(skullMeta);

        return head;
    }

    public static ItemStack createCustomHeadItem(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        // Direkt vom Online-Spieler
        skullMeta.setPlayerProfile(player.getPlayerProfile());

        head.setItemMeta(skullMeta);
        return head;
    }
}