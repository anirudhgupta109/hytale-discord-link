package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

import java.util.Map;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class InteractionEventImpl implements InteractionEvent {
    private final SlashCommandInteractionEvent event;
    private final Map<String, InteractionCommandArgument> arguments;

    public InteractionEventImpl(SlashCommandInteractionEvent event, Map<String, InteractionCommandArgument> arguments) {
        this.event = event;
        this.arguments = arguments;
    }

    @Override
    public InteractionCommandArgument getArgument(String name) {
        return arguments.get(name);
    }

    @Override
    public String getStringArgument(String name) {
        return ((InteractionCommandArgumentImpl) arguments.get(name)).getAsString();
    }

    @Override
    public void reply(String message) {
        event.reply(message).setEphemeral(true).queue();
    }

    @Override
    public InteractionMember getMember() {
        return new InteractionMemberImpl(event.getMember().getId());
    }
}
