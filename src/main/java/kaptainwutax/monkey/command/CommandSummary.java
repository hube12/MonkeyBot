package kaptainwutax.monkey.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kaptainwutax.monkey.holder.HolderChannel;
import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Channels;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.entities.TextChannelImpl;

public class CommandSummary extends Command {

    public CommandSummary(String prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        if(!message.getMember().hasPermission(Permission.ADMINISTRATOR))return;

        if(rawCommand.startsWith("setSummaryChannel")) {
            this.setSummaryChannel(message, StrUtils.removeFirstTrim(rawCommand, "setSummaryChannel"));
        } else if(rawCommand.startsWith("addChannel")) {
            this.addChannel(message, StrUtils.removeFirstTrim(rawCommand, "addChannel"));
        } else if(rawCommand.startsWith("removeChannel")) {
            this.removeChannel(message, StrUtils.removeFirstTrim(rawCommand, "removeChannel"));
        } else if(rawCommand.startsWith("setDescription")) {
            this.setDescription(message, StrUtils.removeFirstTrim(rawCommand, "setDescription"));
        } else if(rawCommand.startsWith("resetDescription")) {
            this.resetDescription(message, StrUtils.removeFirstTrim(rawCommand, "resetDescription"));
        } else if(rawCommand.startsWith("calibrateSummaryMessage")) {
            this.calibrateSummaryMessage(message, StrUtils.removeFirstTrim(rawCommand, "calibrateSummaryMessage"));
        } else {
            return;
        }

        /*Should print all the data, but net.dv8tion.jda.api.entities.User
        doesn't serialize properly. A type adapter is required.*/

        //GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setPrettyPrinting();

        //Gson gson = gsonBuilder.create();
        //String json = gson.toJson(Guilds.instance());
        //System.out.println(json);
    }

    public void setSummaryChannel(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));

        removeSummaryChannel(message);
        server.summaryChannel = StrUtils.getChannelId(channelId);

        TextChannel summaryChannel = message.getGuild().getTextChannelById(server.summaryChannel);

        summaryChannel.sendMessage("Couldn't setup the summary channel...").queue(msg -> {
            server.summaryMessageId = msg.getIdLong();
            server.updateSummaryMessage(message);
        });
    }

    public void removeSummaryChannel(MessageReceivedEvent message) {
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));
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
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));

        TextChannel targetChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(channelId));

        if(targetChannel == null) {
            Log.print(message.getTextChannel(), "The channel specified doesn't exist.");
        } else if(Channels.isChannelInSummary(server, channelId)) {
            Log.print(message.getTextChannel(), "The channel specified is already in summary.");
        } else {
            server.channels.add(new HolderChannel(targetChannel));
        }

        server.updateSummaryMessage(message);
    }

    public void removeChannel(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));

        TextChannel targetChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(channelId));
        server.channels.remove(new HolderChannel(targetChannel));

        server.updateSummaryMessage(message);
    }

    public void setDescription(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));

        params = params.trim();
        String channelId = params.split(" ")[0];
        String description = StrUtils.removeFirstTrim(params, channelId);

        for(HolderChannel channel: server.channels) {
            if(channel.getIdAsMessage().equals(channelId)) {
                channel.setDescription(description);
            }
        }

        server.updateSummaryMessage(message);
    }

    public void resetDescription(MessageReceivedEvent message, String channelId) {
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));

        for(HolderChannel channel: server.channels) {
            if(channel.getIdAsMessage().equals(channelId)) {
                channel.resetDescription();
            }
        }

        server.updateSummaryMessage(message);
    }


    private void calibrateSummaryMessage(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().registerServer(new HolderGuild(message.getGuild()));

        String channelId = params.split(" ")[0];
        String messageId = StrUtils.removeFirstTrim(params, channelId);
        channelId = StrUtils.getChannelId(channelId);

        TextChannel channel = server.guild.getTextChannelById(channelId);

        if(channel == null) {
            Log.print(message.getTextChannel(), "The channel specified doesn't exist.");
            return;
        }

        server.summaryChannel = channelId;
        server.summaryMessageId = Long.parseLong(messageId);

        final String[] summaryMessage = {"Couldn't fetch."};

        channel.retrieveMessageById(server.summaryMessageId).queue(message1 -> {
            summaryMessage[0] = message1.getContentRaw();
            summaryMessage[0] = StrUtils.removeFirstTrim(summaryMessage[0], "Channel Summaries :");
            System.out.println(summaryMessage[0]);

            server.channels.clear();
            String[] entries = summaryMessage[0].split("\n");

            for(String entry : entries) {
                String[] summaryMessageParams = entry.trim().split(": ");
                this.addChannel(message, summaryMessageParams[0]);
                this.setDescription(message, summaryMessageParams[1]);
            }
        });
    }

}
