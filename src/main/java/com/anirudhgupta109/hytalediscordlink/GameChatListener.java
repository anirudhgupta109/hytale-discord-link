package com.anirudhgupta109.hytalediscordlink;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;

public class GameChatListener {

    private final DiscordBot discordBot;
    private final Config config;

    public GameChatListener(DiscordBot discordBot, Config config) {
        this.discordBot = discordBot;
        this.config = config;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getSender().getUsername() + ": " + event.getContent();
        discordBot.sendMessage(message);
    }
}
