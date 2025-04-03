package me.a8kj.reply.ticket.properties;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TicketReply {

    private final String messageId;
    private final String currentChannelId;
    private final TicketReference currentReference;

    private final Pair destinationPair;

    @Data
    public static class Pair {
        private final String channeld;
        private final TicketReference messageLocate;
    }

}
