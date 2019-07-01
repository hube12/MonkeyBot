package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderChannel;
import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Channels;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSummary extends Command {

    public CommandSummary(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        if(!message.getMember().hasPermission(Permission.ADMINISTRATOR))return;

        if(rawCommand.startsWith("setSummaryChannel")) {
            this.setSummaryChannel(message, StrUtils.removeFirstTrim(rawCommand, "setSummaryChannel"));
        } else if(rawCommand.startsWith("addChannel")) {
            this.addChannel(message, StrUtils.removeFirstTrim(rawCommand, "addChannel"), false);
        } else if(rawCommand.startsWith("removeChannel")) {
            this.removeChannel(message, StrUtils.removeFirstTrim(rawCommand, "removeChannel"), false);
        } else if(rawCommand.startsWith("setDescription")) {
            this.setDescription(message, StrUtils.removeFirstTrim(rawCommand, "setDescription"), false);
        } else if(rawCommand.startsWith("resetDescription")) {
            this.resetDescription(message, StrUtils.removeFirstTrim(rawCommand, "resetDescription"), false);
        } else if(rawCommand.startsWith("calibrateSummaryMessage")) {
            this.calibrateSummaryMessage(message);
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
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        removeSummaryChannel(message);
        server.summaryChannel = StrUtils.getChannelId(channelId);

        server.createSummaryMessage(message);
    }

    public void removeSummaryChannel(MessageReceivedEvent message) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        server.deleteSummaryMessage(message);
        server.summaryChannel = null;
        server.summaryMessageIds = new String[0];
    }

    public void addChannel(MessageReceivedEvent message, String channelId, boolean caching) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(channelId.equals("ALL_CHANNELS")) {
            for(TextChannel channel : message.getGuild().getTextChannels()) {
                this.addChannel(message, channel.getId(), true);
            }

            server.updateSummaryMessage(message);
            return;
        }

        TextChannel targetChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(channelId));

        if(targetChannel == null) {
            Log.print(message.getTextChannel(), "The channel specified doesn't exist.");
        } else if(Channels.isChannelInSummary(server, channelId)) {
            Log.print(message.getTextChannel(), "The channel specified is already in summary.");
        } else {
            server.channels.add(new HolderChannel(targetChannel));
        }

        if(!caching) server.updateSummaryMessage(message);
    }

    public void removeChannel(MessageReceivedEvent message, String channelId, boolean caching) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(channelId.equals("ALL_CHANNELS")) {
            for(TextChannel channel : message.getGuild().getTextChannels()) {
                this.removeChannel(message, channel.getId(), true);
            }

            server.updateSummaryMessage(message);
            return;
        }

        TextChannel targetChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(channelId));
        server.channels.remove(new HolderChannel(targetChannel));

        if(!caching) server.updateSummaryMessage(message);
    }

    public void setDescription(MessageReceivedEvent message, String params, boolean caching) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        params = params.trim();
        String channelId = params.split(" ")[0];
        String description = StrUtils.removeFirstTrim(params, channelId);

        for(HolderChannel channel : server.channels) {
            if(channel.getIdAsMessage().equals(channelId)) {
                channel.setDescription(description);
            }
        }

        if(!caching) server.updateSummaryMessage(message);
    }

    public void resetDescription(MessageReceivedEvent message, String channelId, boolean caching) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        for(HolderChannel channel : server.channels) {
            if(channel.getIdAsMessage().equals(channelId)) {
                TextChannel channelObj = message.getGuild().getTextChannelById(channelId);
                if (channelObj != null)
                    channel.setDescription(channelObj.getTopic());
            }
        }

        if(!caching) server.updateSummaryMessage(message);
    }


    private void calibrateSummaryMessage(MessageReceivedEvent message) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));
        server.updateSummaryMessage(message);
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "setSummaryChannel <#channel> ` : Sets the summary channel.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "addChannel <#channel> ` : Add a channel to summary.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "removeChannel <#channel> ` : Remove a channel from summary.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "setDescription <#channel> <message> ` : Sets the description of a channel.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "resetDescription <#channel> ` : Resets the description of a channel.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "calibrateSummaryMessage` : If I go offline, run this command to refresh the summary message."
        };
    }

}
