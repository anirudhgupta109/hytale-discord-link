package com.anirudhgupta109.hytalediscordlink;


import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.io.PacketHandler;

import javax.annotation.Nonnull;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerListener extends DeathSystems.OnDeathSystem {

    private final Config config;
    private final AccountManager accountManager;
    private DiscordBot discordBot; // Changed to non-final
    private final Map<UUID, PendingPlayer> pendingPlayers = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public PlayerListener(Config config, AccountManager accountManager, DiscordBot discordBot) {
        this.config = config;
        this.accountManager = accountManager;
        this.discordBot = discordBot;
    }

    public void setDiscordBot(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    public void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        discordBot.sendMessage(":arrow_right: " + playerRef.getUsername() + " has joined!");

        if (config.isAuthEnabled() && !accountManager.isLinked(playerRef.getUuid())) {
            String code = generateCode();
            pendingPlayers.put(playerRef.getUuid(), new PendingPlayer(playerRef, code, System.currentTimeMillis()));

            // Freeze player
            scheduler.schedule(() -> {
                CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "player effect apply " + playerRef.getUsername() + " freeze");
                CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "player effect apply " + playerRef.getUsername() + " stun");
            }, 1, TimeUnit.SECONDS);

            playerRef.sendMessage(Message.raw("Please link your Discord account by sending /link " + code + " to the bot in the designated channel.").color("yellow"));

            scheduler.schedule(() -> {
                PendingPlayer pp = pendingPlayers.remove(playerRef.getUuid());
                if (pp != null) {
                    pp.getPlayerRef().sendMessage(Message.raw("You failed to link your Discord account in time.").color("red"));
                    pp.getPlayerRef().getPacketHandler().disconnect("You failed to link your Discord account in time.");
                }
            }, 90, TimeUnit.SECONDS);
        }
    }

    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        pendingPlayers.remove(event.getPlayerRef().getUuid());
        discordBot.sendMessage(":arrow_left: " + event.getPlayerRef().getUsername() + " has left!");
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef = (PlayerRef) store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
            Message deathMessage = component.getDeathMessage();
            if (deathMessage != null) {
                String message = deathMessage.getAnsiMessage().replace("You were", "was");
                discordBot.sendMessage(":skull: " + playerRef.getUsername() + " " + message);
            } else {
                discordBot.sendMessage(":skull: " + playerRef.getUsername() + " has died!");
            }
        }
    }

    public Map<UUID, PendingPlayer> getPendingPlayers() {
        return pendingPlayers;
    }

    public void removePendingPlayer(UUID uuid) {
        PendingPlayer pendingPlayer = pendingPlayers.remove(uuid);
        if (pendingPlayer != null) {
            // Unfreeze player
            CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "player effect clear " + pendingPlayer.getPlayerRef().getUsername());
        }
    }

    private String generateCode() {
        return String.format("%06d", random.nextInt(999999));
    }

    public static class PendingPlayer {
        private final PlayerRef playerRef;
        private final String code;
        private final long timestamp;

        public PendingPlayer(PlayerRef playerRef, String code, long timestamp) {
            this.playerRef = playerRef;
            this.code = code;
            this.timestamp = timestamp;
        }

        public PlayerRef getPlayerRef() {
            return playerRef;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}