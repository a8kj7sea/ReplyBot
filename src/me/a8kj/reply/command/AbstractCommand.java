package me.a8kj.reply.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@RequiredArgsConstructor
@Getter
public abstract class AbstractCommand implements Command {

    private final String name;
    private final String prefix;

    @Override
    public String getDescription() {
        return "No description provided.";
    }

    @Override
    public String getUsage() {
        return getPrefix() + getName();
    }

    @Override
    public abstract void perform(GuildMessageReceivedEvent event);

    public boolean test(Member member) {
        if (getPermissions().isEmpty() || getPermissions() == null)
            return true;
        return member.hasPermission(getPermissions());
    }
}
