package com.vunilly.vanillie.pvp;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class PvpListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPvp(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();

        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }


        AttributeInstance scale = attacker.getAttribute(Attribute.SCALE);
        if (scale.getBaseValue() <= 0.8) {
            attacker.sendMessage(Lang.get("msg.pvp.tooLow.you").getFirst());
            event.setCancelled(true);
            return;
        }
        AttributeInstance theirScale = victim.getAttribute(Attribute.SCALE);
        if (theirScale.getBaseValue() <= 0.8) {
            attacker.sendMessage(
                    Lang.get("msg.pvp.tooLow.them", Placeholder.parsed("player", victim.getName())).getFirst());
            event.setCancelled(true);
            return;
        }

        if (PvpManager.isPvpOff(attacker.getUniqueId())) {
            attacker.sendActionBar(
                    Lang.get("msg.pvp.cancelled.you").getFirst());
            event.setCancelled(true);
            return;
        }

        if (PvpManager.isPvpOff(victim.getUniqueId())) {
            attacker.sendActionBar(
                    Lang.get(
                            "msg.pvp.cancelled.them",
                            Placeholder.parsed("player", victim.getName())).getFirst());
            event.setCancelled(true);
            return;
        }

        // Only reached if PvP is enabled for BOTH players
        PvpManager.startCombat(attacker.getUniqueId());
        PvpManager.startCombat(victim.getUniqueId());

    }
}