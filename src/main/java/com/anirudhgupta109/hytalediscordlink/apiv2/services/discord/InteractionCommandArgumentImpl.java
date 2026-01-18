package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

public class InteractionCommandArgumentImpl extends InteractionCommandArgument {
    private final String value;

    public InteractionCommandArgumentImpl(String name, String description, InteractionCommandArgumentType type, boolean required, String value) {
        super(name, description, type, required);
        this.value = value;
    }

    public String getAsString() {
        return value;
    }
}
