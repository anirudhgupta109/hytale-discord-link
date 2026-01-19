package com.anirudhgupta109.hytalediscordlink;

import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionControllerImpl;
import com.anirudhgupta109.hytalediscordlink.commands.LinkCommand;
import com.anirudhgupta109.hytalediscordlink.commands.UnlinkCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.hypixel.hytale.server.api.plugin.pluginmanager.PermissionsModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DiscordBot {

    private JDA jda;
    private final Config config;
    private final GameBroadcaster gameBroadcaster;
    private final AccountManager accountManager;
    private final PlayerListener playerListener;
    private final PermissionsModule permissionsModule;
    private InteractionControllerImpl interactionController;
    private HytaleDiscordLinkPlugin plugin; // Added reference to the main plugin

    public DiscordBot(Config config, GameBroadcaster gameBroadcaster, AccountManager accountManager, PlayerListener playerListener, PermissionsModule permissionsModule) {
        this.config = config;
        this.gameBroadcaster = gameBroadcaster;
        this.accountManager = accountManager;
        this.playerListener = playerListener;
        this.permissionsModule = permissionsModule;
    }

    // Setter for the plugin instance, to be called after plugin initialization
    public void setPlugin(HytaleDiscordLinkPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        try {
            interactionController = new InteractionControllerImpl(this);
            jda = JDABuilder.createDefault(config.getBotToken())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new DiscordChatListener(gameBroadcaster, accountManager, playerListener, config), interactionController, new DiscordRoleListener(this, accountManager))
                    .build();
            jda.awaitReady();
            interactionController.registerCommand(new LinkCommand(accountManager, this, config));
            interactionController.registerCommand(new UnlinkCommand(accountManager, this, config));
            interactionController.registerCommand(new SyncRolesCommand(accountManager, this, config, permissionsModule));
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
        jda.getGuildById(Long.parseLong(config.getGuildId())).getTextChannelById(Long.parseLong(config.getChannels().get("primary"))).sendMessage(message).queue();
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

    public Config getConfig() {
        return config;
    }

    public HytaleDiscordLinkPlugin getPlugin() {
        return plugin;
    }

    public void syncDiscordRolesToHytaleGroups(UUID playerUUID, String discordUserId) {
        if (!config.isRoleSyncEnabled()) {
            return;
        }

        jda.retrieveUserById(discordUserId).queue(discordUser -> {
            jda.getGuildById(config.getGuildId()).retrieveMember(discordUser).queue(member -> {
                Map<String, String> roleMappings = config.getDiscordRoleToHytaleGroupMap();
                List<String> playerHytaleGroups = permissionsModule.getGroupsForUser(playerUUID);
                
                // Get all roles currently mapped to a Hytale group from the config
                // This is to ensure we only remove groups that are managed by this plugin's sync
                Map<String, String> managedRoleMappings = new HashMap<>();
                for (Map.Entry<String, String> entry : roleMappings.entrySet()) {
                    managedRoleMappings.put(entry.getValue(), entry.getKey()); // Store Hytale Group -> Discord Role ID
                }

                for (Map.Entry<String, String> entry : roleMappings.entrySet()) {
                    String discordRoleId = entry.getKey();
                    String hytaleGroupName = entry.getValue();

                    boolean hasDiscordRole = member.getRoles().stream()
                            .anyMatch(role -> role.getId().equals(discordRoleId));

                    if (hasDiscordRole) {
                        if (!playerHytaleGroups.contains(hytaleGroupName)) {
                            permissionsModule.addUserToGroup(playerUUID, hytaleGroupName);
                            plugin.getLogger().at(Level.INFO).log("Added player " + playerUUID + " to Hytale group " + hytaleGroupName + " based on Discord role.");
                        }
                    } else {
                        // Only remove if the Hytale group is actively managed by a Discord role in our config
                        if (playerHytaleGroups.contains(hytaleGroupName) && managedRoleMappings.containsKey(hytaleGroupName)) {
                            permissionsModule.removeUserFromGroup(playerUUID, hytaleGroupName);
                            plugin.getLogger().at(Level.INFO).log("Removed player " + playerUUID + " from Hytale group " + hytaleGroupName + " due to Discord role change.");
                        }
                    }
                }
            }, failure -> {
                plugin.getLogger().at(Level.WARNING).log("Failed to retrieve Discord member for user ID: " + discordUserId + " - " + failure.getMessage());
            });
        }, failure -> {
            plugin.getLogger().at(Level.WARNING).log("Failed to retrieve Discord user for ID: " + discordUserId + " - " + failure.getMessage());
        });
    }

    public void clearSyncedHytaleGroups(UUID playerUUID) {
        if (!config.isRoleSyncEnabled()) {
            return;
        }

        List<String> playerHytaleGroups = permissionsModule.getGroupsForUser(playerUUID);
        Map<String, String> roleMappings = config.getDiscordRoleToHytaleGroupMap();

        for (Map.Entry<String, String> entry : roleMappings.entrySet()) {
            String hytaleGroupName = entry.getValue();
            if (playerHytaleGroups.contains(hytaleGroupName)) {
                permissionsModule.removeUserFromGroup(playerUUID, hytaleGroupName);
                plugin.getLogger().at(Level.INFO).log("Removed player " + playerUUID + " from Hytale group " + hytaleGroupName + " after unlinking Discord account.");
            }
        }
    }
