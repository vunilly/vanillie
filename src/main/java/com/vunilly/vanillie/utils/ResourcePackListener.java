package com.vunilly.vanillie.utils;

import java.net.URI;
import java.util.HexFormat;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.vunilly.vanillie.utils.Lang;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;

public class ResourcePackListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(
                        ResourcePackInfo.resourcePackInfo(
                                UUID.randomUUID(),
                                URI.create("https://github.com/vunilly/vanillie/releases/download/v1.0/Vanillie-RP-v1.0.zip"),
                                "f214bf06c398edb17ab2733c10451ea21c6d4dbf"
                        )
                )
                .required(true)
                .prompt(Lang.get("msg.rp.txt").getFirst())
                .build();

        event.getPlayer().sendResourcePacks(request);
    }
}