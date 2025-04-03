package me.a8kj.reply.ticket.repo;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import lombok.NonNull;
import me.a8kj.reply.repository.AbstractRepository;
import me.a8kj.reply.ticket.Ticket;
import me.a8kj.reply.ticket.properties.TicketReference;
import me.a8kj.reply.ticket.properties.TicketReply;
import me.a8kj.reply.ticket.properties.TicketReply.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class TicketRepository extends AbstractRepository<Ticket, String> {

    private static final String HUB_GUILD_ID = "1357253586693918831";
    private static final String DEVELOPMENT_GUILD_ID = "1357252955484590080";
    private static final String HUB_TICKET_CATEGORY = "1357263948570820699";
    private static final String DEVELOPMENT_TICKET_CATEGORY = "1357271116879757323";
    private static final String TICKET_EMBED_TITLE = "Support Ticket Created";
    private static final String TICKET_EMBED_DESC = "Your ticket has been created. Our team will assist you shortly.";
    private static final String REPLY_EMBED_TITLE = "Reply to Freelancer";
    private static final String REPLY_EMBED_DESC = "To contact the freelancer, reply to this message using Discord's built-in reply feature.";

    @Override
    protected String getId(Ticket ticket) {
        return ticket.getChannelId();
    }

    public void createTicket(JDA jda, Member member, ButtonClickEvent event) {
        Guild hubGuild = jda.getGuildById(HUB_GUILD_ID);
        Guild developmentGuild = jda.getGuildById(DEVELOPMENT_GUILD_ID);

        if (hubGuild == null || developmentGuild == null) {
            return;
        }

        TextChannel hubChannel = createTicketChannelForMember(hubGuild, member, HUB_TICKET_CATEGORY);
        TextChannel developmentChannel = createTicketChannelForDeveloper(developmentGuild, member,
                DEVELOPMENT_TICKET_CATEGORY);

        if (hubChannel == null || developmentChannel == null) {
            return;
        }

        Ticket ticket = new Ticket(hubChannel.getId(), member.getId());
        ticket.setSectionRoleId("1357313072624304289");

        sendEmbedMessage(hubChannel, createEmbed(TICKET_EMBED_TITLE, TICKET_EMBED_DESC));
        sendEmbedMessage(developmentChannel, createEmbed(REPLY_EMBED_TITLE, REPLY_EMBED_DESC));

        var replyMessageInHub = sendEmbedMessage(hubChannel, createEmbed(REPLY_EMBED_TITLE, REPLY_EMBED_DESC));
        var replyMessageInDev = sendEmbedMessage(developmentChannel, createEmbed("Reply to Client",
                "To contact the client, reply to this message using Discord's built-in reply feature."));

        ticket.assignTicketReply(TicketReference.MAIN_SERVER,
                new TicketReply(replyMessageInDev, hubChannel.getId(), TicketReference.MAIN_SERVER,
                        new Pair(hubChannel.getId(), TicketReference.MAIN_SERVER)));

        ticket.assignTicketReply(TicketReference.DEVELOPMENT_SERVER,
                new TicketReply(replyMessageInHub, developmentChannel.getId(), TicketReference.DEVELOPMENT_SERVER,
                        new Pair(developmentChannel.getId(), TicketReference.DEVELOPMENT_SERVER)));

        this.create(ticket);
    }

    private String sendEmbedMessage(TextChannel channel, EmbedBuilder embed) {
        return channel.sendMessageEmbeds(embed.build())
                .submit()
                .thenApply(message -> message.getId())
                .join();
    }

    private EmbedBuilder createEmbed(String title, String description) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(Color.GREEN);
    }

    private TextChannel createTicketChannelForMember(@NonNull Guild guild, @NonNull Member member, String categoryId) {
        Category category = getCategoryById(categoryId, guild);
        if (category == null) {
            return null;
        }

        TextChannel textChannel = guild.createTextChannel(member.getUser().getName() + "-ticket", category).complete();
        setMemberPermissions(textChannel, guild, member.getId(), false);
        return textChannel;
    }

    private TextChannel createTicketChannelForDeveloper(@NonNull Guild guild, @NonNull Member member,
            String categoryId) {
        Category category = getCategoryById(categoryId, guild);
        if (category == null) {
            return null;
        }

        TextChannel textChannel = guild.createTextChannel(member.getUser().getName() + "-ticket", category).complete();
        setDeveloperPermissions(textChannel, guild);
        return textChannel;
    }

    private void setMemberPermissions(TextChannel textChannel, Guild guild, String memberId,
            boolean removePermissions) {
        guild.retrieveMemberById(memberId).queue(member -> {
            if (removePermissions) {
                textChannel.getPermissionOverrides().stream()
                        .filter(override -> override.getMember() != null && override.getMember().equals(member))
                        .forEach(override -> override.delete().queue());
                return;
            }

            Collection<Permission> allowPermissions = Arrays.asList(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_ADD_REACTION);

            textChannel.getManager().putPermissionOverride(member, allowPermissions, Collections.emptyList()).queue();

        }, throwable -> {
            System.out.println("Error retrieving member: " + throwable.getMessage());
        });
    }

    private void setDeveloperPermissions(TextChannel textChannel, Guild guild) {
        Role supportRole = guild.getRoleById("1357313072624304289");

        if (textChannel.getPermissionOverrides().stream()
                .anyMatch(override -> override.getRole() != null && override.getRole().equals(supportRole))) {
            return;
        }

        textChannel.createPermissionOverride(guild.getPublicRole())
                .setDeny(Permission.VIEW_CHANNEL)
                .queue();

        if (supportRole != null) {
            textChannel.createPermissionOverride(supportRole)
                    .setAllow(Permission.VIEW_CHANNEL)
                    .queue();
        }
    }

    private Category getCategoryById(String categoryId, Guild guild) {
        return guild.getCategoryById(categoryId);
    }
}
