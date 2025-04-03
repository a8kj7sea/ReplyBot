package me.a8kj.reply.command.impl;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.a8kj.reply.command.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class CreatePanelCommand extends AbstractCommand {

    private static final String COMMAND_NAME = "create";
    private static final String COMMAND_PREFIX = "!";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred while processing your request.";

    public CreatePanelCommand() {
        super(COMMAND_NAME, COMMAND_PREFIX);
    }

    @Override
    public void perform(GuildMessageReceivedEvent event) {
        try {
            EmbedBuilder embed = createEmbed();
            sendEmbedMessage(event, embed);
        } catch (Exception e) {
            handleError(e, event);
        }
    }

    private EmbedBuilder createEmbed() {
        return new EmbedBuilder()
                .setTitle("Create Ticket")
                .setDescription(
                        "To create a ticket order, please interact with the button to create an order for MC plugin development.")
                .setColor(Color.GREEN);
    }

    private void sendEmbedMessage(GuildMessageReceivedEvent event, EmbedBuilder embed) {
        Button createButton = Button.success("create_ticket_button", "Create Ticket");
        ActionRow actionRow = ActionRow.of(createButton);

        event.getChannel().sendMessage(embed.build())
                .setActionRows(actionRow)
                .queue();
    }

    private void handleError(Exception e, GuildMessageReceivedEvent event) {
        e.printStackTrace();

        event.getChannel().sendMessage(GENERAL_ERROR_MESSAGE)
                .mentionRepliedUser(true)
                .reference(event.getMessage())
                .queue(message -> {
                    message.addReaction("\u274C").queue();
                    message.delete().queueAfter(5, TimeUnit.SECONDS);
                });
    }

    @Override
    public Set<Permission> getPermissions() {
        return Collections.singleton(Permission.ADMINISTRATOR);
    }
}
