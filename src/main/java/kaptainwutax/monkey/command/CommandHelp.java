package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandHelp extends Command {

    public CommandHelp(String[] prefix) {
        super(prefix);
    }

    //TODO: Get a better command system for this.
    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        if(!server.controller.funCommands && !message.getMember().hasPermission(Permission.ADMINISTRATOR))return;

        rawCommand = this.removePrefix(rawCommand);

        StringBuilder helpMessage = new StringBuilder();

        for(Command command: Commands.COMMANDS) {
            for(String line: command.getCommandDesc()) {
                helpMessage.append(line);
                helpMessage.append('\n');
            }
        }

        message.getAuthor().openPrivateChannel().queue(dms -> {
            for (String submsg : StrUtils.splitMessage(helpMessage.toString())) {
                dms.sendMessage(submsg).queue();
            }
            message.getChannel().sendMessage("Help has been sent to you via DMs").queue();
        });
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "` : Shows all the commands, you already know this."
        };
    }

}
