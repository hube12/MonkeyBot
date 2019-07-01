package kaptainwutax.monkey.command;

import kaptainwutax.monkey.init.Commands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPing extends Command {

    public CommandPing(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        message.getChannel().sendMessage("Pong!").queue();
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "` : Ping pong..."
        };
    }

}
