package kaptainwutax.monkey.command;

import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.stream.Stream;

public abstract class Command {

    private String[] prefix;

    public Command(String[] prefix) {
        this.prefix = prefix;
    }

    protected String[] getPrefix() {
        return prefix;
    }

    public abstract void processCommand(MessageReceivedEvent message, String rawCommand);

    public abstract String[] getCommandDesc();

    public String getPrefixDesc() {
        StringBuilder prefix = new StringBuilder();

        for(int i = 0; i < this.getPrefix().length; i++) {
            prefix.append(this.getPrefix()[i]);
            if(i + 1 == this.getPrefix().length)break;
            prefix.append("/");
        }

        return prefix.append(' ').toString();
    }

    public boolean isCommand(String command) {
        return (Stream.of(this.getPrefix()).anyMatch(s -> command.trim().startsWith(s)));
    }

    public String removePrefix(String command) {
        return StrUtils.removeFirstTrim(command, Stream.of(this.getPrefix()).filter(s -> command.trim().startsWith(s)).findFirst().get());
    }

}
