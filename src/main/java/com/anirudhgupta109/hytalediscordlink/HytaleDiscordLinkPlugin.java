package com.anirudhgupta109.hytalediscordlink;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;

public class HytaleDiscordLinkPlugin extends JavaPlugin {

    private Config config;
    private DiscordBot discordBot;
    private AccountManager accountManager;
    private GameBroadcaster gameBroadcaster;
    private PlayerListener playerListener;

    public HytaleDiscordLinkPlugin(JavaPluginInit javaPluginInit) {
        super(javaPluginInit);
    }

    private long startTime;

    @Override
    protected void setup() {
        startTime = System.currentTimeMillis();
        getLogger().at(Level.INFO).log("Setting up...");
        File dataFolder = getDataDirectory().toFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", configFile);
        }
        config = Config.load(configFile);
        if (config == null) {
            getLogger().at(Level.WARNING).log("Failed to load config.yml. Disabling plugin.");
            return;
        }
        getLogger().at(Level.INFO).log("Configuration loaded.");

        accountManager = new AccountManager(dataFolder);
        gameBroadcaster = new GameBroadcaster();
        
        // Initialize playerListener temporarily without discordBot to break the circular dependency
        playerListener = new PlayerListener(config, accountManager, null); 
        discordBot = new DiscordBot(config, gameBroadcaster, accountManager, playerListener, PermissionsModule.get());
        discordBot.setPlugin(this); // Set the plugin instance
        accountManager.setDiscordBot(discordBot); // Set the discordBot in AccountManager
        // Now that discordBot is initialized, set it in playerListener
        playerListener.setDiscordBot(discordBot);


        getLogger().at(Level.INFO).log("Setup complete.");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("Starting...");
        if (config == null) {
            getLogger().at(Level.SEVERE).log("Cannot start: configuration not loaded.");
            return;
        }
        discordBot.start();

        double startupTime = (System.currentTimeMillis() - startTime) / 1000.0;
        discordBot.sendMessage(":white_check_mark: The server has started in " + String.format("%.2f", startupTime) + " seconds!");

        GameChatListener gameChatListener = new GameChatListener(discordBot, config);
        getEventRegistry().registerAsync(PlayerChatEvent.class, null, (Function<CompletableFuture<PlayerChatEvent>, CompletableFuture<PlayerChatEvent>>) future -> future.thenApply(event -> {
            gameChatListener.onPlayerChat(event);
            return event;
        }));

        getEventRegistry().register(PlayerConnectEvent.class, playerListener::onPlayerConnect);
        getEventRegistry().register(PlayerDisconnectEvent.class, playerListener::onPlayerDisconnect);
        getEntityStoreRegistry().registerSystem(playerListener);
        getCommandRegistry().register(new SyncRolesCommand(accountManager, discordBot, config, PermissionsModule.get()));



        getLogger().at(Level.INFO).log("Hytale Discord Link plugin enabled!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("Shutting down...");
        if (discordBot != null) {
            discordBot.sendMessage(":octagonal_sign: The server has stopped!");
            discordBot.stop();
        }
        getLogger().at(Level.INFO).log("Hytale Discord Link plugin disabled!");
    }

    private void saveResource(String resourcePath, File targetFile) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                getLogger().at(Level.SEVERE).log("Resource " + resourcePath + " not found.");
                return;
            }
            Files.copy(in, targetFile.toPath());
        } catch (IOException e) {
            getLogger().at(Level.SEVERE).log("Failed to save resource " + resourcePath + ": " + e.getMessage());
        }
    }
}
