package kaptainwutax.monkey.init;

import kaptainwutax.monkey.command.Command;
import kaptainwutax.monkey.command.CommandMonkey;
import kaptainwutax.monkey.command.CommandPing;
import kaptainwutax.monkey.command.CommandSummary;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public static List<Command> COMMANDS = new ArrayList<Command>();

    public static Command MONKEY = new CommandMonkey("monkey");

    public static Command PING = new CommandPing("ping");
    public static Command PING2 = new CommandPing("\uD83C\uDFD3");
    public static Command SUMMARY = new CommandSummary("summary");


    public static void registerCommand(Command command) {
        if(!COMMANDS.contains(command))COMMANDS.add(command);
    }

    public static void registerCommands() {
        registerCommand(MONKEY);
        registerCommand(PING);
        registerCommand(PING2);
        registerCommand(SUMMARY);
    }

}
