package kaptainwutax.monkey.init;

import kaptainwutax.monkey.command.*;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public static List<Command> COMMANDS = new ArrayList<Command>();

    public static Command MONKEY = new CommandMonkey("monkey");
    public static Command MONKEY2 = new CommandMonkey("\uD83D\uDC12");

    public static Command PING = new CommandPing("ping");
    public static Command PING2 = new CommandPing("\uD83C\uDFD3");
    public static Command SAY = new CommandSay("say");

    public static Command SUMMARY = new CommandSummary("summary");


    public static void registerCommand(Command command) {
        if(!COMMANDS.contains(command)) COMMANDS.add(command);
    }

    public static void registerCommands() {
        registerCommand(MONKEY);
        registerCommand(MONKEY2);
        registerCommand(PING);
        registerCommand(PING2);
        registerCommand(SAY);
        registerCommand(SUMMARY);
    }

}
