package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.utility.CactusSimulation;
import kaptainwutax.monkey.utility.MathHelper;

import static kaptainwutax.monkey.command.arguments.MultibaseLongArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandCactus {

    private static final String[] MESSAGE = {"...", ", very lame.", " jrek.", " ¯\\_(ツ)_/¯.", ", you have high IQ.", ", impressive.", " epic brainer.", " you legend."};

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("cactus", "Returns the height of the cactus in that chunk seed.")
            .requires(MessageCommandSource::canUseFunCommands)
            .then(argument("seed", multibaseLong())
                .executes(ctx -> cactus(ctx.getSource(), getMultibaseLong(ctx, "seed"), 63))
                .then(argument("floorLevel", multibaseLong())
                    .executes(ctx -> cactus(ctx.getSource(), getMultibaseLong(ctx, "seed"), (int)getMultibaseLong(ctx, "floorLevel"))))));
    }

    private static int cactus(MessageCommandSource source, long seed, int floorLevel) {
        CactusSimulation cactusSimulation = new CactusSimulation(CactusSimulation.DESERT, floorLevel);
        int cactusHeight = cactusSimulation.populate(seed);

        source.getChannel().sendMessage("You found a " + cactusHeight + " tall cactus" + MESSAGE[MathHelper.clamp(cactusHeight, 0, MESSAGE.length - 1)]).queue();
        return cactusHeight;
    }

}
