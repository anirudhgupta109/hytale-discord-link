package com.anirudhgupta109.hytalediscordlink;

import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionControllerImpl;
import com.anirudhgupta109.hytalediscordlink.commands.LinkCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

    private JDA jda;
    private final Config config;
    private final GameBroadcaster gameBroadcaster;
    private final AccountManager accountManager;
    private final PlayerListener playerListener;
    private InteractionControllerImpl interactionController;

    public DiscordBot(Config config, GameBroadcaster gameBroadcaster, AccountManager accountManager, PlayerListener playerListener) {
        this.config = config;
        this.gameBroadcaster = gameBroadcaster;
        this.accountManager = accountManager;
        this.playerListener = playerListener;
    }

    public void start() {
        try {
            interactionController = new InteractionControllerImpl(this);
            jda = JDABuilder.createDefault(config.getBotToken())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                    .addEventListeners(new DiscordChatListener(gameBroadcaster, accountManager, playerListener, config), interactionController)
                    .build();
            jda.awaitReady();
            interactionController.registerCommand(new LinkCommand(accountManager, this));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public void sendMessage(String message) {
        jda.getGuildById(Long.parseLong(config.getGuildId())).getTextChannelById(Long.parseLong(config.getChannelId())).sendMessage(message).queue();
    }

    public GameBroadcaster getGameBroadcaster() {
        return gameBroadcaster;
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public JDA getJda() {
        return jda;
    }
}
