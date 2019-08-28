package kaptainwutax.monkey.command;

import kaptainwutax.monkey.MonkeyBot;
import kaptainwutax.monkey.init.Commands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandMonkey extends Command {

    public CommandMonkey() {
        super(null);
    }

    @Override
    protected String[] getPrefix() {
        return MonkeyBot.instance().config.commandPrefix;
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

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + "` : Yes, that's my name."
        };
    }

}
