package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

public class InteractionMemberImpl implements InteractionMember {
    private final String id;

    public InteractionMemberImpl(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
