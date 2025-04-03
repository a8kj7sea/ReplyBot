package me.a8kj.reply.program;

import java.util.logging.Logger;

import lombok.Getter;
import me.a8kj.reply.command.impl.CreatePanelCommand;
import me.a8kj.reply.command.listener.CommandReceivedListener;
import me.a8kj.reply.command.repo.CommandRepository;
import me.a8kj.reply.listener.ButtonInteractionListener;
import me.a8kj.reply.listener.ReplyReceivedListener;
import me.a8kj.reply.object.BotObject;
import me.a8kj.reply.ticket.repo.TicketRepository;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Getter
public class ReplyProgram implements ProgramCycle {

    private final Logger logger = Logger.getLogger("ReplyProgramBot");

    private TicketRepository ticketRepository;
    private CommandRepository commandRepository;
    private BotObject replyBot;

    @Override
    public void onEnable() {
        this.replyBot = new BotObject("bot-token");
        this.replyBot.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS
        );

        this.ticketRepository = new TicketRepository();
        this.commandRepository = new CommandRepository();
        this.commandRepository.create(new CreatePanelCommand());
        this.replyBot.registerListeners(
                new CommandReceivedListener(commandRepository),
                new ButtonInteractionListener(ticketRepository),
                new ReplyReceivedListener(ticketRepository)
        );
        this.replyBot.build();
    }

    @Override
    public void onDisable() {
    }
}
