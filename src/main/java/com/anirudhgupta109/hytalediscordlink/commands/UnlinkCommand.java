package com.anirudhgupta109.hytalediscordlink.commands;

import com.anirudhgupta109.hytalediscordlink.AccountManager;
import com.anirudhgupta109.hytalediscordlink.Config;
import com.anirudhgupta109.hytalediscordlink.DiscordBot;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionCommand;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionCommandArgument;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.Message;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UnlinkCommand implements InteractionCommand {

    private final AccountManager accountManager;
    private final DiscordBot discordBot;
    private final Config config;

    public UnlinkCommand(AccountManager accountManager, DiscordBot discordBot, Config config) {
        this.accountManager = accountManager;
        this.discordBot = discordBot;
        this.config = config;
    }

    @Override
    public String getName() {
        return "unlink";
    }

    @Override
    public String getDescription() {
        return "Unlink your Discord account from your Hytale account.";
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return Collections.emptyList();
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void onCommand(InteractionEvent event) {
        String discordId = event.getMember().getId();
        UUID playerUUID = accountManager.getPlayerUUIDByDiscordId(discordId);

        if (playerUUID == null) {
            event.reply("You must be logged into the Hytale server to unlink your account. This is a security measure.");
            return;
        }

        PlayerRef playerRef = discordBot.getPlayerListener().getPlayer(playerUUID);
        if (playerRef == null) {
            // This case should ideally not happen if getPlayerUUIDByDiscordId returned a UUID,
            // but it's good practice to check.
            event.reply("An error occurred. Could not find your player on the server, even though you appear to be authenticated.");
            accountManager.removeActiveSession(playerUUID); // Clean up inconsistent state
            return;
        }

        accountManager.removeFullAccount(playerUUID);

        playerRef.sendMessage(Message.raw("Your Hytale and Discord accounts have been unlinked.").color("red"));
        
        discordBot.getPlayerListener().initiateLinkingProcess(playerRef);

        event.reply("Your Hytale and Discord accounts have been successfully unlinked. Please check in-game to re-link your account.");
    }
}
