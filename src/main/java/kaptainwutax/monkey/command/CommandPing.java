package kaptainwutax.monkey.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPing extends Command {

    public CommandPing(String prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        message.getChannel().sendMessage("Pong!").queue();
    }

}
