package kaptainwutax.monkey;

import net.dv8tion.jda.api.entities.TextChannel;

public class HolderChannel {

    private TextChannel channel;
    private String id;

    public HolderChannel(TextChannel channel) {
        this.channel = channel;
        this.id = channel.getId();
    }

    public TextChannel getChannel() {
        return this.channel;
    }

    public String getId() {
        return this.id;
    }

    public String getIdAsMessage() {
        return "<@" + this.id + ">";
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(id);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof HolderChannel ? ((HolderChannel)obj).getId().equals(this.id) : false);
    }
}