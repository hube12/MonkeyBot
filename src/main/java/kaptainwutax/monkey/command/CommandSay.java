package kaptainwutax.monkey.command;

import kaptainwutax.monkey.utility.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSay extends Command {

    public CommandSay(String prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        message.getChannel().sendMessage(rawCommand).queue();
    }

}
