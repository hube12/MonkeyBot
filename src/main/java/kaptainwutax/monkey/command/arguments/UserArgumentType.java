package kaptainwutax.monkey.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kaptainwutax.monkey.MonkeyBot;
import kaptainwutax.monkey.command.Exceptions;
import kaptainwutax.monkey.command.MessageCommandSource;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class UserArgumentType implements ArgumentType<UserArgumentType.UserGetter> {

    private static final Collection<String> EXAMPLES = Arrays.asList("word", "123", "<@user>", "<@!user>");

    private UserArgumentType() {}

    public static UserArgumentType user() {
        return new UserArgumentType();
    }

    public static <S> UserGetter getUserGetter(CommandContext<S> context, String argName) {
        return context.getArgument(argName, UserGetter.class);
    }

    public static User getUser(CommandContext<MessageCommandSource> context, String argName) throws CommandSyntaxException {
        return getUserGetter(context, argName).getUser(context.getSource());
    }

    @Override
    public UserGetter parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '<') {
            reader.skip();
            reader.expect('@');
            if (reader.canRead() && reader.peek() == '!')
                reader.skip();
            long userId = reader.readLong();
            reader.expect('>');
            return source -> getUserById(source, userId);
        } else {
            int start = reader.getCursor();
            try {
                long userId = reader.readLong();
                return source -> getUserById(source, userId);
            } catch (CommandSyntaxException e) {
                reader.setCursor(start);
                String userName = reader.readString();
                return source -> getUserByName(source, userName);
            }
        }
    }

    private static User getUserById(MessageCommandSource source, long userId) throws CommandSyntaxException {
        User user = MonkeyBot.instance().jda.getUserById(userId);
        if (user == null)
            throw Exceptions.NO_SUCH_USER.create(userId);
        return user;
    }

    private static User getUserByName(MessageCommandSource source, String userName) throws CommandSyntaxException {
        List<User> users = MonkeyBot.instance().jda.getUsersByName(userName, true);
        if (users.isEmpty())
            throw Exceptions.NO_SUCH_USER.create(userName);
        if (users.size() == 1)
            return users.get(0);
        users = MonkeyBot.instance().jda.getUsersByName(userName, false);
        if (users.size() != 1)
            throw Exceptions.USER_AMBIGUOUS_EXCEPTION.create(userName);
        return users.get(0);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public interface UserGetter {
        User getUser(MessageCommandSource source) throws CommandSyntaxException;
    }
}
