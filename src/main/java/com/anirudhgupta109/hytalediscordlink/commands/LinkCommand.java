package com.anirudhgupta109.hytalediscordlink.commands;

import com.anirudhgupta109.hytalediscordlink.AccountManager;
import com.anirudhgupta109.hytalediscordlink.DiscordBot;
import com.anirudhgupta109.hytalediscordlink.PlayerListener;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionCommand;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionCommandArgument;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionCommandArgumentType;
import com.anirudhgupta109.hytalediscordlink.apiv2.services.discord.InteractionEvent;

import java.util.Collections;
import java.util.List;

public class LinkCommand implements InteractionCommand {

    private final AccountManager accountManager;
    private final DiscordBot discordBot;

    public LinkCommand(AccountManager accountManager, DiscordBot discordBot) {
        this.accountManager = accountManager;
        this.discordBot = discordBot;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDescription() {
        return "Link your Discord account to your Hytale account.";
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return Collections.singletonList(
                new InteractionCommandArgument("code", "The link code you received in-game.", InteractionCommandArgumentType.STRING, true)
        );
    }

    @Override
    public void onCommand(InteractionEvent event) {
        String code = event.getStringArgument("code");
        PlayerListener.PendingPlayer pendingPlayer = discordBot.getPlayerListener().getPendingPlayers().values().stream()
                .filter(pp -> pp.getCode().equals(code))
                .findFirst()
                .orElse(null);

        if (pendingPlayer != null) {
            accountManager.linkAccount(pendingPlayer.getPlayerRef().getUuid(), event.getMember().getId());
            discordBot.getPlayerListener().removePendingPlayer(pendingPlayer.getPlayerRef().getUuid());
            event.reply("Your account has been successfully linked!");
        } else {
            event.reply("Invalid link code.");
        }
    }
}

