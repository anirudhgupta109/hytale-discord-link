package com.anirudhgupta109.hytalediscordlink.apiv2.services.discord;

import com.anirudhgupta109.hytalediscordlink.DiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InteractionControllerImpl extends ListenerAdapter implements InteractionController {
    private final Map<String, InteractionCommand> commands = new HashMap<>();
    private final DiscordBot bot;

    public InteractionControllerImpl(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void registerCommand(InteractionCommand command) {
        System.out.println("Registering command: " + command.getName());
        commands.put(command.getName(), command);
        bot.getJda().upsertCommand(command.getName(), command.getDescription())
                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING, "code", "The link code you received in-game.", true)
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("Received slash command: " + event.getName());
        InteractionCommand command = commands.get(event.getName());
        if (command != null) {
            System.out.println("Found command: " + command.getName());
            Map<String, InteractionCommandArgument> arguments = new HashMap<>();
            for (OptionMapping option : event.getOptions()) {
                System.out.println("Option: " + option.getName() + " = " + option.getAsString());
                arguments.put(option.getName(), new InteractionCommandArgumentImpl(option.getName(), "", InteractionCommandArgumentType.STRING, true, option.getAsString()));
            }
            command.onCommand(new InteractionEventImpl(event, arguments));
        } else {
            System.out.println("Command not found: " + event.getName());
        }
    }
}
