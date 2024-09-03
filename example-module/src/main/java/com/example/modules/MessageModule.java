package com.example.modules;

import com.example.modules.listener.MessageListener;
import eu.koboo.minestom.api.module.Module;
import eu.koboo.minestom.api.module.annotation.ModuleInfo;
import eu.koboo.minestom.api.server.Server;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.util.logging.Logger;

@ModuleInfo(
        name = "MessageModule",
        version = "1.0.0",
        description = "A module that displays a message on the screen",
        authors = {"Your Name"},
        moduleDependencies = {
                //No dependencies, but you can add them here
        }
)
public class MessageModule extends Module {

    Logger logger;

    @Override
    public void onEnable() {
        logger = Logger.getLogger("MessageModule");
        Server.getInstance().getModuleManager().registerListener(PlayerSpawnEvent.class, new MessageListener());
        logger.info("MessageModule has been enabled!");
    }

    @Override
    public void onDisable() {
        logger.info("MessageModule has been disabled!");
    }
}