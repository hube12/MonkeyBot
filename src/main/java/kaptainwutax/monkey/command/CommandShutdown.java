package kaptainwutax.monkey.command;

import kaptainwutax.monkey.MonkeyBot;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.utility.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandShutdown extends Command {
    public CommandShutdown(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        if (MonkeyBot.instance().config.botAdmins.contains(message.getAuthor().getId())) {
            Log.print(message.getTextChannel(), "Shutting down...");
            MonkeyBot.instance().shutdown();
        } else {
            Log.print(message.getTextChannel(), "No u. Go get permission.");
        }
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "`: Shuts down the global bot instance. (BOT ADMIN ONLY)"
        };
    }
}
