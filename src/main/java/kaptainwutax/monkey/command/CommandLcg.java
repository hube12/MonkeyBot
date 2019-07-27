package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLcg extends Command {

    private static final long MULTIPLIER = 0x5deece66dL;
    private static final long ADDEND = 0xbL;
    private static final long MASK = 0xffffffffffffL;

    public CommandLcg(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        if (!server.controller.funCommands && !message.getMember().hasPermission(Permission.ADMINISTRATOR))
            return;
        
        rawCommand = removePrefix(rawCommand);
        String[] params = rawCommand.split(" ");

        if (params.length == 1 && params[0].isEmpty()) {

            Log.print(message.getTextChannel(), formatLCGConstants(MULTIPLIER, ADDEND));

        } else if (params[0].equals("combine")) {

            long n;
            try {
                n = parseSeed(params[1]);
            } catch (NumberFormatException e) {
                Log.print(message.getTextChannel(), "Not a number");
                return;
            }

            LCG lcg = combine(n);

            Log.print(message.getTextChannel(), formatLCGConstants(lcg.multiplier, lcg.addend));

        } else if (params[0].equals("next") || params[0].equals("previous")) {

            long seed;
            long n;
            try {
                seed = parseSeed(params[1]);
                n = params.length > 2 ? parseSeed(params[2]) : 1;
            } catch (NumberFormatException e) {
                Log.print(message.getTextChannel(), "Not a number");
                return;
            }

            LCG lcg = combine(params[0].equals("next") ? n : -n);

            seed = seed * lcg.multiplier + lcg.addend;
            seed &= MASK;

            Log.print(message.getTextChannel(), formatHexDec(seed));

        }
    }

    @Override
    public String[] getCommandDesc() {
        final String PREFIX = Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc();
        return new String[] {
                "`" + PREFIX + "`: prints the constants for the Java LCG",
                "`" + PREFIX + "combine <n>`: prints the LCG equivalent to calling the Java LCG *n* times",
                "`" + PREFIX + "next <seed> [n=1]`: prints the seed *n* seeds after *seed* in the Java LCG",
                "`" + PREFIX + "previous <seed> [n=1]`: prints the seed *n* seeds before *seed* in the Java LCG. Equivalent to `" + PREFIX + "next <seed> -n`"
        };
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
