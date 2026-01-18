package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

public interface InteractionEvent {
    InteractionCommandArgument getArgument(String name);
    String getStringArgument(String name);
    void reply(String message);
    InteractionMember getMember();
}
