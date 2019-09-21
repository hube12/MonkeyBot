package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import kaptainwutax.monkey.MonkeyBot;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.entities.ChannelType;

import javax.annotation.Nullable;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandHelp {

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("help")
            .executes(ctx -> help(ctx.getSource()))
            .then(argument("command", greedyString())
                .executes(ctx -> help(ctx.getSource(), getString(ctx, "command")))));
    }

    private static int help(MessageCommandSource source) {
        CommandDispatcher<MessageCommandSource> dispatcher = MonkeyBot.instance().dispatcher;

        doHelp(source, dispatcher.getRoot(), null);

        return dispatcher.getRoot().getChildren().size();
    }

    private static int help(MessageCommandSource source, String command) throws CommandSyntaxException {
        String[] commandPath = command.split(" ");
        CommandDispatcher<MessageCommandSource> dispatcher = MonkeyBot.instance().dispatcher;

        CommandNode<MessageCommandSource> node = dispatcher.getRoot();
        for (String subcmd : commandPath) {
            boolean found = false;
            for (CommandNode<MessageCommandSource> child : node.getChildren()) {
                if (child instanceof LiteralCommandNode && ((LiteralCommandNode<MessageCommandSource>) child).getLiteral().equals(subcmd) && child.canUse(source)) {
                    node = child;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
            }
        }

        assert node instanceof LiteralCommandNode;
        doHelp(source, node, Commands.getHelp((LiteralCommandNode<MessageCommandSource>) node));

        return node.getChildren().size();
    }

    private static void doHelp(MessageCommandSource source, CommandNode<MessageCommandSource> node, @Nullable String customHeader) {
        CommandDispatcher<MessageCommandSource> dispatcher = MonkeyBot.instance().dispatcher;

        StringBuilder helpMessage = new StringBuilder();
        if (customHeader == null)
            helpMessage.append("Run `help <command>` for descriptions of each command. Supports subcommands after spaces.\n**Note that the help command only lists commands you may use in the channel you issued the command**.");
        else
            helpMessage.append(customHeader);

        for (String usageText : dispatcher.getSmartUsage(node, source).values()) {
            helpMessage.append("\n- ").append(usageText);
        }

        source.getUser().openPrivateChannel().queue(dms -> {
            for (String submsg : StrUtils.splitMessage(helpMessage.toString())) {
                dms.sendMessage(submsg).queue();
            }
        });

        if (source.getChannel().getType() != ChannelType.PRIVATE)
            source.getChannel().sendMessage("Help has been sent to you via DMs").queue();
    }

}
