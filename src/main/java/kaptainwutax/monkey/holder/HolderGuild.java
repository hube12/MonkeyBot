package kaptainwutax.monkey.holder;

import kaptainwutax.monkey.utility.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HolderGuild {

    public Guild guild;
    public String id;
    public List<HolderChannel> channels = new ArrayList<HolderChannel>();
    public String summaryChannel = null;
    public long summaryMessageId = 0;

    public HolderGuild(Guild guild) {
        this.guild = guild;
        this.id = guild.getId();
    }

    public void updateSummaryMessage(MessageReceivedEvent message) {
        if(summaryChannel == null) {
            Log.print(message.getTextChannel(), "Summary channel doesn't exist.");
            return;
        }

        Log.edit(this.guild.getTextChannelById(this.summaryChannel), this.summaryMessageId, this.getSummaryMessage());
    }

    public String getSummaryMessage() {
        String message = "Channel Summaries : \n";

        for (HolderChannel channel : this.channels) {
            message += channel.getIdAsMessage() + ": " + channel.getDescription() + "\n";
        }

        return message;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        HolderGuild target = ((HolderGuild) obj);
        return target.id.equals(this.id);
    }

}
