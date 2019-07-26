package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.CactusSimulation;
import kaptainwutax.monkey.utility.MathHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandCactus extends Command {

    private static final String[] MESSAGE = {"...", ", very lame.", " jrek.", " ¯\\_(ツ)_/¯.", ", you have high IQ.", ", impressive.", " epic brainer.", " you legend."};

    public CommandCactus(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        if(!server.controller.funCommands && !message.getMember().hasPermission(Permission.ADMINISTRATOR))return;

        rawCommand = this.removePrefix(rawCommand);

        long seed;

        try {seed = Long.parseLong(rawCommand);}
        catch(Exception e) {return;}

        CactusSimulation cactusSimulation = new CactusSimulation(CactusSimulation.DESERT, 62);
        int cactusHeight = cactusSimulation.populate(seed);

        message.getChannel().sendMessage("You found a " + cactusHeight + " tall cactus" + MESSAGE[MathHelper.clamp(cactusHeight, 0, MESSAGE.length - 1)]).queue();
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "<seed> ` : Returns the height cactus in that chunk seed."
        };
    }

}
