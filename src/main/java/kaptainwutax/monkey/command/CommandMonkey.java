package kaptainwutax.monkey.command;

import kaptainwutax.monkey.init.Commands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandMonkey extends Command {

    public CommandMonkey(String prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        for(Command command : Commands.COMMANDS) {
            if(command.isCommand(rawCommand)) {
                command.processCommand(message, rawCommand);
                return;
            }
        }
    }

}
