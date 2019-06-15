package kaptainwutax.monkey.init;

import kaptainwutax.monkey.holder.HolderGuild;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Guilds {

    private static Guilds INSTANCE;

    public List<HolderGuild> SERVERS = new ArrayList<HolderGuild>();

    public static Guilds instance() {
        if(INSTANCE == null) INSTANCE = new Guilds();
        return INSTANCE;
    }

    public HolderGuild registerServer(HolderGuild server) {
        if(!this.isServerRegistered(server)) {
            this.SERVERS.add(server);
            return server;
        }

        return this.getServerFromId(server.getId());
    }

    public void unregisterServer(HolderGuild server) {
        if(this.isServerRegistered(server)) {
            this.SERVERS.remove(server);
        }
    }

    public boolean isServerRegistered(HolderGuild server) {
        for(HolderGuild s : this.SERVERS) {
            if(server.equals(s)) return true;
        }

        return false;
    }

    @Nullable
    public HolderGuild getServerFromId(String guildId) {
        for(HolderGuild server : this.SERVERS) {
            if(server.getId().equals(guildId)) {
                return server;
            }
        }

        return null;
    }

    @Nullable
    public HolderGuild getServerFromGuild(Guild guild) {
        return this.getServerFromId(guild.getId());
    }

}
