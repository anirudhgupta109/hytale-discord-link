package com.anirudhgupta109.hytalediscordlink;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.World;

public class GameBroadcaster {

    public void broadcastMessage(Message message) {
        HytaleServer.get().SCHEDULED_EXECUTOR.execute(() -> {
            World world = Universe.get().getDefaultWorld();
            PlayerUtil.broadcastMessageToPlayers(null, message, world.getEntityStore().getStore());
        });
    }
}