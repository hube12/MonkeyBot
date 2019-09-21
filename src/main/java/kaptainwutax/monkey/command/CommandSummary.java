package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.holder.HolderChannel;
import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Channels;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static kaptainwutax.monkey.command.arguments.ChannelArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandSummary {

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("summary", "Handles summary channels.")
            .requires(MessageCommandSource::isAdministrator)
            .then(literal("setChannel", "Sets the summary channel.")
                .then(argument("channel", channel())
                    .executes(ctx -> setSummaryChannel(ctx.getSource(), getChannel(ctx, "channel")))))
            .then(literal("addAll", "Add all channels to the summary.")
                .executes(ctx -> addAllChannels(ctx.getSource())))
            .then(literal("add", "Add a channel to summary.")
                .then(argument("channel", channel())
                    .executes(ctx -> addChannel(ctx.getSource(), getChannel(ctx, "channel"), false))))
            .then(literal("removeAll", "Remove all channels from the summary (clear the summary).")
                .executes(ctx -> removeAllChannels(ctx.getSource())))
            .then(literal("remove", "Remove a channel from summary.")
                .then(argument("channel", channel())
                    .executes(ctx -> removeChannel(ctx.getSource(), getChannel(ctx, "channel"), false))))
            .then(literal("setDescription", "Sets the description of a channel.")
                .then(argument("channel", channel())
                    .then(argument("description", greedyString())
                        .executes(ctx -> setDescription(ctx.getSource(), getChannel(ctx, "channel"), getString(ctx, "description"))))))
            .then(literal("resetDescription", "Resets the description of a channel to the channel topic.")
                .then(argument("channel", channel())
                    .executes(ctx -> resetDescription(ctx.getSource(), getChannel(ctx, "channel"))))));
    }

    private static int setSummaryChannel(MessageCommandSource source, MessageChannel channel) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        if(server.summaryChannel != null) removeSummaryChannel(source);
        server.summaryChannel = channel.getId();

        server.createSummaryMessage(source.getEvent());

        return 0;
    }

    private static void removeSummaryChannel(MessageCommandSource source) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));
        server.deleteSummaryMessage(source.getEvent());
        server.summaryChannel = null;
        server.summaryMessageIds = new String[0];
    }

    private static int addAllChannels(MessageCommandSource source) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        for (TextChannel channel : source.getGuild().getTextChannels()) {
            addChannel(source, channel, true);
        }

        server.updateSummaryMessage(source.getEvent());

        return source.getGuild().getTextChannels().size();
    }

    private static int addChannel(MessageCommandSource source, MessageChannel channel, boolean caching) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        if (Channels.isChannelInSummary(server, channel.getId())) {
            source.getChannel().sendMessage("The channel specified is already in summary.").queue();
        } else {
            server.channels.add(new HolderChannel((TextChannel) channel));
        }

        if(!caching) server.updateSummaryMessage(source.getEvent());

        return 0;
    }

    private static int removeAllChannels(MessageCommandSource source) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        for (TextChannel channel : source.getGuild().getTextChannels()) {
            removeChannel(source, channel, true);
        }

        server.updateSummaryMessage(source.getEvent());

        return source.getGuild().getTextChannels().size();
    }

    private static int removeChannel(MessageCommandSource source, MessageChannel channel, boolean caching) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.channels.remove(new HolderChannel((TextChannel) channel));

        if(!caching) server.updateSummaryMessage(source.getEvent());

        return 0;
    }

    private static int setDescription(MessageCommandSource source, MessageChannel channel, String description) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        for(HolderChannel chan : server.channels) {
            if(chan.getId().equals(channel.getId())) {
                chan.setDescription(description);
            }
        }

        server.updateSummaryMessage(source.getEvent());

        return 0;
    }

    private static int resetDescription(MessageCommandSource source, MessageChannel channel) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        for(HolderChannel chan : server.channels) {
            if(chan.getId().equals(channel.getId())) {
                    chan.setDescription(((TextChannel) channel).getTopic());
            }
        }

        server.updateSummaryMessage(source.getEvent());

        return 0;
    }

}
