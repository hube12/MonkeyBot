package kaptainwutax.monkey.holder;

import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.entities.TextChannel;

public class HolderChannel {

    private String id;
    private String description;

    public HolderChannel(TextChannel channel) {
        this.id = channel.getId();
        this.description = channel.getTopic();
    }

    public String getId() {
        return this.id;
    }

    public String getIdAsMessage() {
        return StrUtils.getChannelIdAsMessage(this.id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(id);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof HolderChannel && ((HolderChannel) obj).getId().equals(this.id));
    }

}