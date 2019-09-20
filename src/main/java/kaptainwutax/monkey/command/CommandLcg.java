package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.init.Commands;

import static kaptainwutax.monkey.command.arguments.MultibaseLongArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandLcg {

    private static final long MULTIPLIER = 0x5deece66dL;
    private static final long ADDEND = 0xbL;
    private static final long MASK = 0xffffffffffffL;

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("lcg", "Prints the constants for the Java LCG")
            .requires(MessageCommandSource::canUseFunCommands)
            .executes(ctx -> printDefaultLCGConstants(ctx.getSource()))
            .then(literal("combine", "Prints the LCG equivalent to calling the Java LCG *n* times")
                .then(argument("n", multibaseLong())
                    .executes(ctx -> combine(ctx.getSource(), getMultibaseLong(ctx, "n")))))
            .then(literal("next", "Prints the seed *n* seeds after *seed* in the Java LCG")
                .then(argument("seed", multibaseLong())
                    .executes(ctx -> next(ctx.getSource(), getMultibaseLong(ctx, "seed"), 1))
                    .then(argument("n", multibaseLong())
                        .executes(ctx -> next(ctx.getSource(), getMultibaseLong(ctx, "seed"), getMultibaseLong(ctx, "n"))))))
            .then(literal("previous", "Prints the seed *n* seeds before *seed* in the Java LCG. Equivalent to `next <seed> -n`")
                .then(argument("seed", multibaseLong())
                    .executes(ctx -> next(ctx.getSource(), getMultibaseLong(ctx, "seed"), -1))
                    .then(argument("n", multibaseLong())
                        .executes(ctx -> next(ctx.getSource(), getMultibaseLong(ctx, "seed"), -getMultibaseLong(ctx, "n")))))));
    }

    private static int printDefaultLCGConstants(MessageCommandSource source) {
        source.getChannel().sendMessage(formatLCGConstants(MULTIPLIER, ADDEND)).queue();
        return 0;
    }

    private static int combine(MessageCommandSource source, long n) {
        LCG lcg = combine(n);
        source.getChannel().sendMessage(formatLCGConstants(lcg.multiplier, lcg.addend)).queue();
        return 0;
    }

    private static int next(MessageCommandSource source, long seed, long n) {
        LCG lcg = combine(n);
        seed = seed * lcg.multiplier + lcg.addend;
        seed &= MASK;
        source.getChannel().sendMessage(formatHexDec(seed)).queue();
        return 0;
    }

    private static String formatLCGConstants(long multiplier, long addend) {
        return "Multiplier: " + formatHexDec(multiplier) + ", Addend: " + formatHexDec(addend);
    }

    private static String formatHexDec(long n) {
        return String.format("0x%X (%d)", n, n);
    }

    private static long parseSeed(String str) throws NumberFormatException {
        long n;
        if (str.startsWith("0x") || str.startsWith("0X")) {
            n = Long.parseLong(str.substring(2), 16);
        } else {
            n = Long.parseLong(str);
        }
        return n & MASK;
    }

    private static LCG combine(long n) {
        /*
         * This works by splitting n into sums of powers of 2, and combining those power of 2 LCGs.
         * An LCG (a1, b1) can be combined with an LCG (a2, b2) to make an LCG (a3, b3) by:
         * s' = a2 * (a1 * s + b1) + b2 = (a1 * a2) * s + (a2 * b1 + b2), hence
         * a3 = a1 * a2
         * b3 = a2 * b1 + b2
         */

        // Start with the identity LCG
        long multiplier = 1;
        long addend = 0;

        // The LCG to combine with at the current step
        long intermediateMultiplier = MULTIPLIER;
        long intermediateAddend = ADDEND;

        // for each bit from right to left
        for (long k = n; k != 0; k >>>= 1) {
            if ((k & 1) != 0) { // if the bit is 1
                // combine the current LCG with the intermediate LCG
                multiplier *= intermediateMultiplier;
                addend = intermediateMultiplier * addend + intermediateAddend;
            }

            // combine the intermediate multiplier with itself
            intermediateAddend = (intermediateMultiplier + 1) * intermediateAddend;
            intermediateMultiplier *= intermediateMultiplier;
        }

        multiplier &= MASK;
        addend &= MASK;
        return new LCG(multiplier, addend);
    }

    private static class LCG {
        private long multiplier;
        private long addend;

        public LCG(long multiplier, long addend) {
            this.multiplier = multiplier;
            this.addend = addend;
        }
    }

}
