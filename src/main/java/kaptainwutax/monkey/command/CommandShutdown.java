package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.MonkeyBot;

import static kaptainwutax.monkey.init.Commands.*;

public class CommandShutdown {

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("shutdown", "Shuts down the global bot instance.")
            .requires(source -> MonkeyBot.instance().config.botAdmins.contains(source.getUser().getId()))
            .executes(ctx -> shutdown(ctx.getSource())));
    }

    private static int shutdown(MessageCommandSource source) {
        source.getChannel().sendMessage("Shutting down...").queue();
        MonkeyBot.instance().shutdown();
        return 0;
    }

}
