package kaptainwutax.monkey.init;

import kaptainwutax.monkey.command.*;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public static List<Command> COMMANDS = new ArrayList<Command>();

    public static Command MONKEY = new CommandMonkey();
    public static Command HELP = new CommandHelp(new String[] {"help"});

    public static Command PING = new CommandPing(new String[] {"ping", "\uD83C\uDFD3"});
    public static Command SAY = new CommandSay(new String[] {"say"});
    public static Command CACTUS = new CommandCactus(new String[] {"cactus"});
    public static Command STRONGHOLD = new CommandStronghold(new String[] {"stronghold"});
    public static Command LCG = new CommandLcg(new String[] {"lcg"});

    public static Command SUMMARY = new CommandSummary(new String[] {"summary"});
    public static Command MOD = new CommandMod(new String[] {"mod"});

    public static Command SHUTDOWN = new CommandShutdown(new String[] {"shutdown"});

    public static void registerCommand(Command command) {
        if(!COMMANDS.contains(command)) COMMANDS.add(command);
    }

    public static void registerCommands() {
        registerCommand(MONKEY);
        registerCommand(HELP);
        registerCommand(PING);
        registerCommand(SAY);
        registerCommand(CACTUS);
        registerCommand(STRONGHOLD);
        registerCommand(LCG);
        registerCommand(SUMMARY);
        registerCommand(MOD);
        registerCommand(SHUTDOWN);
    }

}
