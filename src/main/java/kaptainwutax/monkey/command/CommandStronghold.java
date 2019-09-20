package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.utility.MathHelper;

import java.util.Random;

import static kaptainwutax.monkey.command.arguments.MultibaseLongArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandStronghold {

    private static final String[] MESSAGE = {"...", ", very lame.", " jrek.", " ¯\\_(ツ)_/¯.", ", you have high IQ.", ", impressive.", " epic brainer.", " you legend."};

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("stronghold", "Returns the number of eyes in the stronghold in that chunk seed.")
            .requires(MessageCommandSource::canUseFunCommands)
            .then(argument("seed", multibaseLong())
                .executes(ctx -> stronghold(ctx.getSource(), getMultibaseLong(ctx, "seed")))));
    }

    private static int stronghold(MessageCommandSource source, long seed) {
        Random random = new Random(seed ^ 0x5DEECE66DL);
        int count = 0;

        for(int i = 0; i < 12; i++) {
            float r = random.nextFloat();
            if(r > 0.9f) count++;
        }

        source.getChannel().sendMessage("You found a " + count + " eye stronghold" + MESSAGE[MathHelper.clamp(count, 0, MESSAGE.length - 1)]).queue();

        return count;
    }

}
