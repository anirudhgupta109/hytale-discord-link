package com.anirudhgupta109.hytalediscordlink;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class LinkCommand extends AbstractTargetPlayerCommand {

    private final AccountManager accountManager;

    public LinkCommand(AccountManager accountManager) {
        super("link", "Links your discord account");
        this.accountManager = accountManager;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        String code = accountManager.generateLinkCode(playerRef.getUuid());
        playerRef.sendMessage(Message.raw("Your link code is: " + code).color("yellow"));
        playerRef.sendMessage(Message.raw("Please send this code to the Discord bot to link your account.").color("yellow"));
    }
}
