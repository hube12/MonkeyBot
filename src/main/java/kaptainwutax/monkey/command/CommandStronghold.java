package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.MathHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandStronghold extends Command {

    private static final String[] MESSAGE = {"...", ", very lame.", " jrek.", " ¯\\_(ツ)_/¯.", ", you have high IQ.", ", impressive.", " epic brainer.", " you legend."};

    public CommandStronghold(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        if(!server.controller.funCommands && !message.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        rawCommand = this.removePrefix(rawCommand);

        long seed;

        try {
            seed = Long.parseLong(rawCommand);
        } catch(Exception e) {
            return;
        }

        Random random = new Random(seed ^ 0x5DEECE66DL);
        int count = 0;

        for(int i = 0; i < 12; i++) {
            float r = random.nextFloat();
            if(r > 0.9f) count++;
            System.out.println(r);
        }

        message.getChannel().sendMessage("You found a " + count + " eye stronghold" + MESSAGE[MathHelper.clamp(count, 0, MESSAGE.length - 1)]).queue();
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "<seed> ` : Returns the number of eyes in the stronghold in that chunk seed."
        };
    }

}
