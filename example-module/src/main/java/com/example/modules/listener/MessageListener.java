package com.example.modules.listener;

import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.entity.Player;
import net.kyori.adventure.text.Component;
import java.util.function.Consumer;

public class MessageListener implements Consumer<PlayerSpawnEvent> {

    @Override
    public void accept(PlayerSpawnEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.text("Welcome to the server!"));
    }

}