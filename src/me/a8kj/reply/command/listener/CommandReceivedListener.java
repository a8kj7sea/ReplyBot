package me.a8kj.reply.command.listener;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import me.a8kj.reply.command.AbstractCommand;
import me.a8kj.reply.command.repo.CommandRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class CommandReceivedListener extends ListenerAdapter {

    private final CommandRepository commandRepository;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String messageContent = event.getMessage().getContentRaw().trim();
        String[] args = messageContent.split("\\s+");

        if (args.length == 0) {
            return;
        }

        String commandWithPrefix = args[0];

        Optional<AbstractCommand> optionalCommand = commandRepository.findAll().stream()
                .filter(cmd -> commandWithPrefix.startsWith(cmd.getPrefix()))
                .findFirst();

        if (optionalCommand.isEmpty()) {
            return;
        }

        AbstractCommand command = optionalCommand.get();
        String commandLabel = commandWithPrefix.substring(command.getPrefix().length()).toLowerCase();

        if (commandLabel.isEmpty()) {
            return;
        }

        if (!messageContent.startsWith(command.getPrefix() + commandLabel)) {
            return;
        }

        Member member = event.getMember();

        if (member == null)
            return;
        if (!command.test(member)) {
            sendPermissionError(event);
            return;
        }

        command.perform(event);
    }

    private void sendPermissionError(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("You don't have permission to execute this command!")
                .mentionRepliedUser(true)
                .reference(event.getMessage())
                .queue(message -> {
                    message.addReaction("\u274C").queue();
                    message.delete().queueAfter(5, TimeUnit.SECONDS);
                });
    }
}
