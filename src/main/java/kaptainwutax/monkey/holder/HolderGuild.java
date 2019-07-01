package kaptainwutax.monkey.holder;

import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public final class HolderGuild {

    public String id;
    public List<HolderChannel> channels = new ArrayList<>();
    public String summaryChannel = null;
    public String[] summaryMessageIds = new String[0];

    private HolderGuild() {} // serialization

    public HolderGuild(Guild guild) {
        this.id = guild.getId();
    }

    public boolean deleteSummaryMessage(MessageReceivedEvent commandMessage) {
        TextChannel channel = getSummaryChannel(commandMessage);
        if (channel == null)
            return false;

        for (String summaryMsg : summaryMessageIds)
            Log.delete(channel, summaryMsg);

        return true;
    }

    public boolean createSummaryMessage(MessageReceivedEvent commandMessage) {
        TextChannel channel = getSummaryChannel(commandMessage);
        if (channel == null)
            return false;

        String[] summary = StrUtils.splitMessage(generateSummaryMessage());
        summaryMessageIds = new String[summary.length];

        for (int i = 0; i < summary.length; i++) {
            final int index = i;
            channel.sendMessage(summary[i]).queue(msg -> summaryMessageIds[index] = msg.getId());
        }

        return true;
    }

    public boolean updateSummaryMessage(MessageReceivedEvent commandMessage) {
        return deleteSummaryMessage(commandMessage) & createSummaryMessage(commandMessage);
    }

    public String generateSummaryMessage() {
        StringBuilder message = new StringBuilder("Channel Summaries : \n");

        for(HolderChannel channel : this.channels) {
            message.append(channel.getIdAsMessage()).append(": ").append(channel.getDescription()).append("\n");
        }

        return message.toString();
    }

    public String getId() {
        return this.id;
    }

    private TextChannel getSummaryChannel(MessageReceivedEvent commandMessage) {
        TextChannel channel = summaryChannel == null ? null : commandMessage.getGuild().getTextChannelById(summaryChannel);
        if(channel == null) {
            if (commandMessage != null)
                Log.print(commandMessage.getTextChannel(), "Summary channel doesn't exist.");
        }
        return channel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != HolderGuild.class) return false;
        HolderGuild target = ((HolderGuild) obj);
        return target.id.equals(this.id);
    }

}
