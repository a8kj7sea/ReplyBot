package me.a8kj.reply.ticket;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import me.a8kj.reply.ticket.properties.TicketReference;
import me.a8kj.reply.ticket.properties.TicketReply;

@Data
public class Ticket {
    private final String channelId;
    private final String memberId;
    public String sectionRoleId;

    private Map<TicketReference, TicketReply> ticketReplies = new HashMap<>();

    public void assignTicketReply(TicketReference reference, TicketReply ticketReply) {
        this.ticketReplies.putIfAbsent(reference, ticketReply);
    }
}
