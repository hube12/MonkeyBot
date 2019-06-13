package kaptainwutax.monkey.command;

import kaptainwutax.monkey.HolderChannel;
import kaptainwutax.monkey.HolderGuild;
import kaptainwutax.monkey.init.Channels;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSummary extends Command {

    public CommandSummary(String prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        if (rawCommand.startsWith("setSummaryChannel")) {
            this.setSummaryChannel(message, rawCommand.substring("setSummaryChannel".length()).trim());
        }
        if (rawCommand.startsWith("addChannel")) {
            this.addChannel(message, rawCommand.substring("addChannel".length()).trim());
        }
        if (rawCommand.startsWith("removeChannel")) {
            this.removeChannel(message, rawCommand.substring("removeChannel".length()).trim());
        }

        message.getChannel().sendMessage("[Finished execution without timeout.]").queue();
    }

    public void setSummaryChannel(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.registerServer(new HolderGuild(message.getGuild()));

        removeSummaryChannel(message);
        server.summaryChannel = channelId.substring(2).replaceFirst(">", "");

        TextChannel summaryChannel = message.getGuild().getTextChannelById(server.summaryChannel);

        long prevMessageId = summaryChannel.getLatestMessageIdLong();

        Log.print(summaryChannel, "Couldn't setup the summary channel...");

        long newPrevMessageId = 0;

        //Hackfix but all I could do. I need to wait for the message to be sent offthread and catch it here.
        do {
            newPrevMessageId = summaryChannel.getLatestMessageIdLong();

            //This is such a big hackfix your eyes will bleed. If I don't slow it down, the thread times out.
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (prevMessageId == newPrevMessageId);

        server.summaryMessageId = newPrevMessageId;
        Log.edit(summaryChannel, newPrevMessageId, server.getSummaryMessage());
    }

    public void removeSummaryChannel(MessageReceivedEvent message) {
        HolderGuild server = Guilds.registerServer(new HolderGuild(message.getGuild()));
        TextChannel summaryChannel = null;
        if (server.summaryChannel != null)
            summaryChannel = message.getGuild().getTextChannelById(server.summaryChannel);

        if (summaryChannel == null || server.summaryMessageId == 0) {
            return;
        }

        Log.delete(summaryChannel, server.summaryMessageId);
        server.summaryChannel = null;
        server.summaryMessageId = 0;
    }

    public void addChannel(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.registerServer(new HolderGuild(message.getGuild()));
        TextChannel summaryChannel = null;

        if (server.summaryChannel != null) {
            summaryChannel = message.getGuild().getTextChannelById(server.summaryChannel);
        } else {
            Log.print(message.getTextChannel(), "Summary channel doesn't exist.");
            return;
        }

        TextChannel targetChannel = message.getGuild().getTextChannelById(channelId.substring(2).replaceFirst(">", ""));

        if (targetChannel != null && !Channels.isChannelInSummary(server, channelId)) {
            server.channels.add(new HolderChannel(targetChannel));
        }

        if (server.summaryMessageId != 0) {
            Log.edit(summaryChannel, server.summaryMessageId, server.getSummaryMessage());
        }
    }

    public void removeChannel(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.registerServer(new HolderGuild(message.getGuild()));
        TextChannel summaryChannel = null;
        if (server.summaryChannel != null)
            summaryChannel = message.getGuild().getTextChannelById(server.summaryChannel);

        TextChannel targetChannel = message.getGuild().getTextChannelById(channelId.substring(2).replaceFirst(">", ""));

        server.channels.remove(new HolderChannel(targetChannel));

        if (server.summaryMessageId != 0 && summaryChannel != null) {
            Log.edit(summaryChannel, server.summaryMessageId, server.getSummaryMessage());
        }
    }

    public void addDescription(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.registerServer(new HolderGuild(message.getGuild()));
    }

    public void removeDescription(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.registerServer(new HolderGuild(message.getGuild()));
    }

}
