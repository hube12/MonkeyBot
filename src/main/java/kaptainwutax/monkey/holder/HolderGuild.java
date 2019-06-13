package kaptainwutax.monkey.holder;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

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

    public String getSummaryMessage() {
        String message = "Channel Summaries : \n";

        for (HolderChannel c : this.channels) {
            TextChannel channel = c.getChannel();

            message += "**" + channel.getName() + "** : " + channel.getTopic() + ".";
            message += "\n";
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
