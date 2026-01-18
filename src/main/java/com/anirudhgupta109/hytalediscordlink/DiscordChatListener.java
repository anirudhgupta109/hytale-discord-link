package com.anirudhgupta109.hytalediscordlink;

import com.anirudhgupta109.hytalediscordlink.PlayerListener.PendingPlayer;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hypixel.hytale.server.core.Message;

import java.util.Map;
import java.util.UUID;

public class DiscordChatListener extends ListenerAdapter {

    private final GameBroadcaster gameBroadcaster;
    private final AccountManager accountManager;
    private final PlayerListener playerListener;
    private final Config config;

    public DiscordChatListener(GameBroadcaster gameBroadcaster, AccountManager accountManager, PlayerListener playerListener, Config config) {
        this.gameBroadcaster = gameBroadcaster;
        this.accountManager = accountManager;
        this.playerListener = playerListener;
        this.config = config;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.getChannel().getId().equals(config.getChannelId())) {
            String messageContent = event.getMessage().getContentRaw();
            User author = event.getAuthor();

            if (messageContent.startsWith("/link ")) {
                String code = messageContent.substring(6).trim();
                boolean linked = false;
                for (Map.Entry<UUID, PendingPlayer> entry : playerListener.getPendingPlayers().entrySet()) {
                    PendingPlayer pendingPlayer = entry.getValue();
                    if (pendingPlayer.getCode().equals(code)) {
                        accountManager.linkAccount(pendingPlayer.getPlayerRef().getUuid(), author.getId());
                        playerListener.removePendingPlayer(pendingPlayer.getPlayerRef().getUuid());
                        event.getChannel().sendMessage("Your Discord account has been linked to Hytale player " + pendingPlayer.getPlayerRef().getUsername() + "!").queue();
                        pendingPlayer.getPlayerRef().sendMessage(Message.raw("<p color='green'>Your Discord account has been successfully linked!</p>"));
                        linked = true;
                        break;
                    }
                }
                if (!linked) {
                    event.getChannel().sendMessage("Invalid or expired link code.").queue();
                }
            } else {
                String text = "[Discord: " + event.getAuthor().getName() + "] " + event.getMessage().getContentDisplay();
                String jsonMessage = "{\"text\": \"" + text + "\", \"color\": \"blue\"}";
                gameBroadcaster.broadcastMessage(jsonMessage);
            }
        }
    }
}
