package me.a8kj.reply.command.repo;

import me.a8kj.reply.command.AbstractCommand;
import me.a8kj.reply.repository.AbstractRepository;

public class CommandRepository extends AbstractRepository<AbstractCommand, String> {

    @Override
    protected String getId(AbstractCommand command) {
        return command.getName().toLowerCase();
    }

    public void register(AbstractCommand abstractCommand) {
        this.create(abstractCommand);
        
    }

}
