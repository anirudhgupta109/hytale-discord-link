package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

public class InteractionCommandArgument {
    private final String name;
    private final String description;
    private final boolean required;

    private final InteractionCommandArgumentType type;

    public InteractionCommandArgument(String name, String description, InteractionCommandArgumentType type, boolean required) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }
}
