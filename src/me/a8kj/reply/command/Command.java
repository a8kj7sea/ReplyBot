package me.a8kj.reply.command;

import java.util.Set;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {

    String getName();

    default String getDescription() {
        return "No description provided.";
    }

    String getUsage();

    String getPrefix();

    Set<Permission> getPermissions();

    void perform(GuildMessageReceivedEvent event);
}
