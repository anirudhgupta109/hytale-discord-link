package com.anirudhgupta109.hytalediscordlink;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

public class DiscordRoleListener extends ListenerAdapter {

    private final DiscordBot discordBot;
    private final AccountManager accountManager;

    public DiscordRoleListener(DiscordBot discordBot, AccountManager accountManager) {
        this.discordBot = discordBot;
        this.accountManager = accountManager;
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        if (!discordBot.getConfig().isRoleSyncEnabled()) {
            return;
        }
        String discordUserId = event.getUser().getId();
        UUID playerUUID = accountManager.getHytalePlayerUUID(discordUserId);
        if (playerUUID != null) {
            discordBot.getPlugin().getLogger().at(Level.INFO).log("Discord roles added for user " + event.getUser().getAsTag() + ". Syncing Hytale groups.");
            discordBot.syncDiscordRolesToHytaleGroups(playerUUID, discordUserId);
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        if (!discordBot.getConfig().isRoleSyncEnabled()) {
            return;
        }
        String discordUserId = event.getUser().getId();
        UUID playerUUID = accountManager.getHytalePlayerUUID(discordUserId);
        if (playerUUID != null) {
            discordBot.getPlugin().getLogger().at(Level.INFO).log("Discord roles removed for user " + event.getUser().getAsTag() + ". Syncing Hytale groups.");
            discordBot.syncDiscordRolesToHytaleGroups(playerUUID, discordUserId);
        }
    }
}
