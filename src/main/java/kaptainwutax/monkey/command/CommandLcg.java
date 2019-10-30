package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.utility.Rand;
import kaptainwutax.monkey.utility.math.LCG;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static kaptainwutax.monkey.command.arguments.MultibaseLongArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandLcg {

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
                        .executes(ctx -> next(ctx.getSource(), getMultibaseLong(ctx, "seed"), -getMultibaseLong(ctx, "n"))))))
            .then(literal("print", "Executes custom LCG command and prints the result.")
                .then(argument("command", greedyString())
                        .executes(ctx -> execute(ctx.getSource(), getString(ctx, "command"))))));

    }

    private static int execute(MessageCommandSource source, String message) {
        String[] calls = message.trim().split(Pattern.quote("."));

        if(calls.length < 1) {
            source.getChannel().sendMessage("Not enough arguments.").queue();
            return 1;
        }

        if(!isValidCall(calls[0], source))return 1;
        IExecutable caller = getCallerObject(calls[0]);

        if(caller == null) {
            source.getChannel().sendMessage("Unknown caller at: [" + calls[0] + "].").queue();
            return 1;
        }

        Object lastObject = caller;

        for(int i = 1; i < calls.length; i++) {
            String call = calls[i];

            if(!isValidCall(call, source))return 1;

            Object returnedObject = caller.callMethod(getName(call), getParams(call));

            if(returnedObject == null) {
                source.getChannel().sendMessage("Unknown call at: [" + call + "].").queue();
                return 1;
            }

            if(returnedObject instanceof IExecutable) {
                caller = (IExecutable)returnedObject;
            } else {
                if(i != calls.length - 1) {
                    source.getChannel().sendMessage("Variable [" + returnedObject.getClass().getName() + "] cannot be used for calls.").queue();
                    return 1;
                }
            }

            lastObject = returnedObject;
        }

        source.getChannel().sendMessage(lastObject.toString()).queue();

        return 0;
    }

    private static String getName(String call) {
        return call.split(Pattern.quote("("))[0].trim().toLowerCase(Locale.ENGLISH);
    }

    private static String[] getParams(String call) {
        StringBuilder paramsRaw = new StringBuilder();
        int bracketDepth = 0;

        for(int i = 0; i < call.length(); i++) {
            char c = call.charAt(i);

            if(c == '(') {
                bracketDepth++;
            } else if(c == ')') {
                bracketDepth--;
            } else if(bracketDepth == 1) {
                paramsRaw.append(c);
            }
        }

        if(paramsRaw.toString().isEmpty())return new String[0];

        String[] params = paramsRaw.toString().split(",");

        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim();
        }

        return params;
    }

    private static IExecutable getCallerObject(String call) {
        String name = getName(call);

        if(name.equalsIgnoreCase("LCG")) {
            return new LCG(0, 0, 0).callConstructor(getParams(call));
        } else if(name.equalsIgnoreCase("Rand")) {
            return new Rand(0).callConstructor(getParams(call));
        }

        return null;
    }

    private static boolean isValidCall(String call, MessageCommandSource source) {
        boolean hasName = false;
        boolean hasBracket = false;
        int bracketDepth = 0;

        for(int i = 0; i < call.length(); i++) {
            char c = call.charAt(i);

            if(c == '(') {
                bracketDepth++;
                if(!hasBracket)hasBracket = true;
            } else if(c == ')') {
                bracketDepth--;
            }

            if(!hasName && !hasBracket && Character.isAlphabetic(c)) {
                hasName = true;
            }
        }

        if(!hasName) {
            source.getChannel().sendMessage("Attempting to call function without a name at: [" + call + "].").queue();
            return false;
        } else if(bracketDepth != 0) {
            source.getChannel().sendMessage("Attempting to call function with invalid number of brackets at: [" + call + "].").queue();
            return false;
        }

        return true;
    }

    private static int printDefaultLCGConstants(MessageCommandSource source) {
        source.getChannel().sendMessage(Rand.JAVA_LCG.toPrettyString()).queue();
        return 0;
    }

    private static int combine(MessageCommandSource source, long n) {
        source.getChannel().sendMessage(Rand.JAVA_LCG.combine(n).toPrettyString()).queue();
        return 0;
    }

    private static int next(MessageCommandSource source, long seed, long n) {
        long nextSeed = Rand.JAVA_LCG.combine(n).nextSeed(seed);
        source.getChannel().sendMessage(String.format("0x%X (%d)", nextSeed, nextSeed)).queue();
        return 0;
    }

    /* OLD CODE
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
         // This works by splitting n into sums of powers of 2, and combining those power of 2 LCGs.
         // An LCG (a1, b1) can be combined with an LCG (a2, b2) to make an LCG (a3, b3) by:
         // s' = a2 * (a1 * s + b1) + b2 = (a1 * a2) * s + (a2 * b1 + b2), hence
         // a3 = a1 * a2
         // b3 = a2 * b1 + b2

        //Start with the identity LCG*
        long multiplier = 1;
        long addend = 0;

        //The LCG to combine with at the current step
        long intermediateMultiplier = MULTIPLIER;
        long intermediateAddend = ADDEND;

        //for each bit from right to left
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
    }*/

}
