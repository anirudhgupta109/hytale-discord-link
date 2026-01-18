package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

import java.util.List;

public interface InteractionCommand {
    boolean isDisabled();
    boolean isEphemeral();
    String getName();
    String getDescription();
    List<InteractionCommandArgument> getArguments();
    void onCommand(InteractionEvent event);
}
