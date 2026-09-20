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
                                URI.create("https://github.com/vunilly/FreshlyFood/releases/download/v1.0/FreshlyFood-RP-v1.0.zip"),
                                "d66feb560211056b0f9025a6575a6df0c808de5c"
                        )
                )
                .required(true)
                .prompt(Lang.get("msg.rp.txt").getFirst())
                .build();

        event.getPlayer().sendResourcePacks(request);
    }
}