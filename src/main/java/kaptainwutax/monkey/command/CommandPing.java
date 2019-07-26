package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPing extends Command {

    public CommandPing(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        if(!server.controller.funCommands && !message.getMember().hasPermission(Permission.ADMINISTRATOR))return;

        message.getChannel().sendMessage("Pong!").queue();
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "` : Ping pong..."
        };
    }

}
