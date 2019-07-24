package kaptainwutax.monkey.command;

import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.utility.Log;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSay extends Command {

    public CommandSay(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        String finalRawCommand = rawCommand;

        if(finalRawCommand == null || finalRawCommand.isEmpty())return;

        message.getChannel().sendMessage("stop abusing me").queue((placeholder) -> {
            Log.edit(placeholder.getTextChannel(), placeholder.getIdLong(), finalRawCommand);
        });
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "<message> ` : Make me say something nice."
        };
    }

}
