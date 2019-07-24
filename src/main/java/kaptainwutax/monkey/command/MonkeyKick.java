package kaptainwutax.monkey.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MonkeyKick extends Command {

    public MonkeyKick(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);
        message.getGuild().getController().kick(rawCommand);
    }

    @Override
    public String[] getCommandDesc() {
        return new String[0];
    }

}
