package kaptainwutax.monkey.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {

    protected String prefix;

    public Command(String prefix) {
        this.prefix = prefix;
    }

    public abstract void processCommand(MessageReceivedEvent message, String rawCommand);

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isCommand(String command) {
        return command.trim().startsWith(this.prefix);
    }

    public String removePrefix(String command) {
        return command.trim().substring(this.prefix.length()).trim();
    }

}
