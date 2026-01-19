package com.anirudhgupta109.hytalediscordlink.commands;

import com.anirudhgupta109.hytalediscordlink.AccountManager;
import com.anirudhgupta109.hytalediscordlink.Config;
import com.anirudhgupta109.hytalediscordlink.DiscordBot;
import com.hypixel.hytale.server.api.plugin.pluginmanager.PermissionsModule;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.AbstractCommand;
import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.command.CommandSource;
import com.hypixel.hytale.server.core.command.annotation.Description;
import com.hypixel.hytale.server.core.command.annotation.Name;
import com.hypixel.hytale.server.core.command.argument.Argument;
import com.hypixel.hytale.server.core.command.argument.types.player.OfflinePlayerArgument;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.logging.Level;

@Name("syncroles")
@Description("Synchronizes Discord roles with Hytale groups for a player.")
@Command(permissions = "hytalediscordlink.command.syncroles")
public class SyncRolesCommand extends AbstractCommand {

    private final AccountManager accountManager;
    private final DiscordBot discordBot;
    private final Config config;
    private final PermissionsModule permissionsModule;

    public SyncRolesCommand(AccountManager accountManager, DiscordBot discordBot, Config config, PermissionsModule permissionsModule) {
        this.accountManager = accountManager;
        this.discordBot = discordBot;
        this.config = config;
        this.permissionsModule = permissionsModule;
    }

    @Override
    public CompletableFuture<Void> execute(@Nonnull CommandSender sender, @Nonnull List<String> args) {
        if (!config.isRoleSyncEnabled()) {
            sender.sendMessage(Message.raw("Discord role synchronization is disabled in the config.").color("red"));
            return CompletableFuture.completedFuture(null);
        }

        if (args.isEmpty()) {
            sender.sendMessage(Message.raw("Usage: /syncroles <player>").color("red"));
            return CompletableFuture.completedFuture(null);
        }

        String targetPlayerName = args.get(0);
        return new OfflinePlayerArgument().parse(sender, Collections.singletonList(targetPlayerName).iterator())
                .thenAccept(playerRef -> {
                    UUID playerUUID = playerRef.getUuid();
                    if (!accountManager.isLinked(playerUUID)) {
                        sender.sendMessage(Message.raw("Player " + playerRef.getUsername() + " is not linked to a Discord account.").color("red"));
                        return;
                    }

                    String discordId = accountManager.getDiscordId(playerUUID);
                    discordBot.syncDiscordRolesToHytaleGroups(playerUUID, discordId);
                    sender.sendMessage(Message.raw("Successfully triggered Discord role synchronization for " + playerRef.getUsername() + ".").color("green"));
                })
                .exceptionally(ex -> {
                    sender.sendMessage(Message.raw("Error: " + ex.getMessage()).color("red"));
                    discordBot.getPlugin().getLogger().at(Level.SEVERE).log("Error executing syncroles command: " + ex.getMessage());
                    return null;
                });
    }

    @Override
    public List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull List<String> args) {
        if (args.size() == 1) {
            String partialName = args.get(0).toLowerCase();
            return discordBot.getPlugin().getOnlinePlayers().stream()
                    .filter(player -> player.getUsername().toLowerCase().startsWith(partialName))
                    .map(PlayerRef::getUsername)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
