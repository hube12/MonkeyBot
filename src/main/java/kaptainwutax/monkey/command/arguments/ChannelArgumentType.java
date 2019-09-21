package kaptainwutax.monkey.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kaptainwutax.monkey.command.Exceptions;
import kaptainwutax.monkey.command.MessageCommandSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChannelArgumentType implements ArgumentType<ChannelArgumentType.ChannelGetter> {

    private static final Collection<String> EXAMPLES = Arrays.asList("word", "123", "<#channel>");

    private ChannelArgumentType() {}

    public static ChannelArgumentType channel() {
        return new ChannelArgumentType();
    }

    public static <S> ChannelGetter getChannelGetter(CommandContext<S> context, String argName) {
        return context.getArgument(argName, ChannelGetter.class);
    }

    public static MessageChannel getChannel(CommandContext<MessageCommandSource> context, String argName) throws CommandSyntaxException {
        return getChannelGetter(context, argName).getChannel(context.getSource());
    }

    @Override
    public ChannelGetter parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '<') {
            reader.skip();
            reader.expect('#');
            long channelId = reader.readLong();
            reader.expect('>');
            return source -> getChannelById(source, channelId);
        } else {
            int start = reader.getCursor();
            try {
                long channelId = reader.readLong();
                return source -> getChannelById(source, channelId);
            } catch (CommandSyntaxException e) {
                reader.setCursor(start);
                String channelName = reader.readString();
                return source -> getChannelByName(source, channelName);
            }
        }
    }

    private static MessageChannel getChannelById(MessageCommandSource source, long channelId) throws CommandSyntaxException {
        if (source.isDMs())
            throw Exceptions.NOT_IN_GUILD.create();
        Guild guild = source.getGuild();
        assert guild != null;
        MessageChannel channel = guild.getTextChannelById(channelId);
        if (channel == null)
            throw Exceptions.NO_SUCH_CHANNEL.create(channelId);
        return channel;
    }

    private static MessageChannel getChannelByName(MessageCommandSource source, String channelName) throws CommandSyntaxException {
        if (source.isDMs())
            throw Exceptions.NOT_IN_GUILD.create();
        Guild guild = source.getGuild();
        assert guild != null;
        List<TextChannel> channels = guild.getTextChannelsByName(channelName, true);
        if (channels.isEmpty())
            throw Exceptions.NO_SUCH_CHANNEL.create(channelName);
        if (channels.size() == 1)
            return channels.get(0);
        channels = guild.getTextChannelsByName(channelName, false);
        if (channels.size() != 1)
            throw Exceptions.CHANNEL_AMBIGUOUS_EXCEPTION.create(channelName);
        return channels.get(0);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public interface ChannelGetter {
        MessageChannel getChannel(MessageCommandSource source) throws CommandSyntaxException;
    }
}
