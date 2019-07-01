package kaptainwutax.monkey.command;

import kaptainwutax.monkey.init.Commands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandHelp extends Command {

    public CommandHelp(String[] prefix) {
        super(prefix);
    }

    //TODO: Get a better command system for this.
    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        StringBuilder helpMessage = new StringBuilder();

        for(Command command: Commands.COMMANDS) {
            for(String line: command.getCommandDesc()) {
                helpMessage.append(line);
                helpMessage.append('\n');
            }
        }

        message.getTextChannel().sendMessage(helpMessage.toString()).queue();
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "` : Shows all the commands, you already know this."
        };
    }

}
