package me.a8kj.reply.listener;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.a8kj.reply.ticket.properties.TicketReference;
import me.a8kj.reply.ticket.properties.TicketReply;
import me.a8kj.reply.ticket.repo.TicketRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
@Getter
public class ButtonInteractionListener extends ListenerAdapter {

    private final TicketRepository ticketRepository;

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButton().getId().equalsIgnoreCase("create_ticket_button")) {
            ticketRepository.createTicket(event.getJDA(), event.getMember(), event);
        } else if (event.getButton().getId().equalsIgnoreCase("close-ticket")) {
            String channelId = event.getChannel().getId();
            ticketRepository.read(channelId).ifPresent(ticket -> {
                ticket.getTicketReplies().values().forEach(ticketReply ->
                        getGuildByReference(ticketReply.getCurrentReference(), event.getJDA())
                                .getTextChannelById(ticketReply.getCurrentChannelId()).delete().queue()
                );
                ticketRepository.delete(channelId);
            });
        }
    }

    private Guild getGuildByReference(TicketReference ticketReference, JDA jda) {
        return ticketReference == TicketReference.DEVELOPMENT_SERVER
                ? jda.getGuildById("1357252955484590080")
                : jda.getGuildById("1357253586693918831");
    }
}
