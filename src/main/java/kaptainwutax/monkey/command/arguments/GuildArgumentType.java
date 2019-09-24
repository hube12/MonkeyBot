package kaptainwutax.monkey.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kaptainwutax.monkey.MonkeyBot;
import kaptainwutax.monkey.command.Exceptions;
import kaptainwutax.monkey.command.MessageCommandSource;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GuildArgumentType implements ArgumentType<GuildArgumentType.GuildGetter> {

    private static final Collection<String> EXAMPLES = Arrays.asList("word", "123");

    private final boolean greedy;

    private GuildArgumentType(boolean greedy) {
        this.greedy = greedy;
    }

    public static GuildArgumentType greedyGuild() {
        return new GuildArgumentType(true);
    }

    public static GuildArgumentType guild() {
        return new GuildArgumentType(false);
    }

    public static <S> GuildGetter getGuildGetter(CommandContext<S> context, String argName) {
        return context.getArgument(argName, GuildGetter.class);
    }

    public static Guild getGuild(CommandContext<MessageCommandSource> context, String argName) throws CommandSyntaxException {
        return getGuildGetter(context, argName).getGuild(context.getSource());
    }

    @Override
    public GuildGetter parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        try {
            long guildId = reader.readLong();
            return source -> getGuildById(source, guildId);
        } catch (CommandSyntaxException e) {
            reader.setCursor(start);
            String guildName;
            if (greedy) {
                guildName = reader.getRemaining();
                reader.setCursor(reader.getTotalLength());
            } else {
                guildName = reader.readString();
            }
            return source -> getGuildByName(source, guildName);
        }
    }

    private static Guild getGuildById(MessageCommandSource source, long guildId) throws CommandSyntaxException {
        Guild guild = MonkeyBot.instance().jda.getGuildById(guildId);
        if (guild == null)
            throw Exceptions.NO_SUCH_GUILD.create(guildId);
        return guild;
    }

    private static Guild getGuildByName(MessageCommandSource source, String guildName) throws CommandSyntaxException {
        List<Guild> guilds = MonkeyBot.instance().jda.getGuildsByName(guildName, true);
        if (guilds.isEmpty())
            throw Exceptions.NO_SUCH_GUILD.create(guildName);
        if (guilds.size() == 1)
            return guilds.get(0);
        guilds = MonkeyBot.instance().jda.getGuildsByName(guildName, false);
        if (guilds.size() != 1)
            throw Exceptions.GUILD_AMBIGUOUS_EXCEPTION.create(guildName);
        return guilds.get(0);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public interface GuildGetter {
        Guild getGuild(MessageCommandSource source) throws CommandSyntaxException;
    }
}
