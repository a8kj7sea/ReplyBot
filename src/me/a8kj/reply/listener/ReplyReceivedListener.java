package me.a8kj.reply.listener;

import java.awt.Color;
import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.a8kj.reply.ticket.Ticket;
import me.a8kj.reply.ticket.properties.TicketReference;
import me.a8kj.reply.ticket.properties.TicketReply;
import me.a8kj.reply.ticket.repo.TicketRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
@Getter
public class ReplyReceivedListener extends ListenerAdapter {

    private final TicketRepository ticketRepository;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getMessage() == null) {
            return;
        }

        Collection<Ticket> tickets = ticketRepository.findAll();

        for (Ticket ticket : tickets) {
            Collection<TicketReply> replies = ticket.getTicketReplies().values();

            replies.stream()
                    .filter(f -> event.getMessage().getReferencedMessage() != null &&
                            event.getMessage().getReferencedMessage().getId().equalsIgnoreCase(f.getMessageId()))
                    .findAny()
                    .ifPresent(ticketReply -> {
                        var guild = getGuildByReference(ticketReply.getDestinationPair().getMessageLocate(),
                                event.getJDA());
                        if (guild == null)
                            return;

                        var channel = guild.getTextChannelById(ticketReply.getDestinationPair().getChanneld());
                        if (channel == null)
                            return;

                        sendEmbedMessage(channel, createEmbed(event.getMember().getId(), event.getMessage()));
                    });
        }
    }

    private EmbedBuilder createEmbed(String senderId, Message message) {
        return new EmbedBuilder()
                .setDescription("<@" + senderId + "> said: \n" + message.getContentRaw())
                .setColor(Color.YELLOW);
    }

    private String sendEmbedMessage(TextChannel channel, EmbedBuilder embed) {
        try {
            return channel.sendMessageEmbeds(embed.build())
                    .submit()
                    .thenApply(message -> message.getId())
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Guild getGuildByReference(TicketReference ticketReference, JDA jda) {
        return ticketReference == TicketReference.DEVELOPMENT_SERVER
                ? jda.getGuildById("1357252955484590080")
                : jda.getGuildById("1357253586693918831");
    }
}
