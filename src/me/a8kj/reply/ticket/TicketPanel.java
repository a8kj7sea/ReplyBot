package me.a8kj.reply.ticket;

import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Data
public class TicketPanel {

    private final String messageId;
    private MessageEmbed embed;
}
