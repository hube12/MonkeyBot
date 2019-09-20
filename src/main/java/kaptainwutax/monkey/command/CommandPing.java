package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;

import static kaptainwutax.monkey.init.Commands.*;

public class CommandPing {

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("ping", "Ping pong...")
            .requires(MessageCommandSource::canUseFunCommands)
            .executes(ctx -> ping(ctx.getSource())));
    }

    private static int ping(MessageCommandSource source) {
        source.getChannel().sendMessage("Pong!").queue();
        return 0;
    }

}
