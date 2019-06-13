package kaptainwutax.monkey.init;

import kaptainwutax.monkey.holder.HolderGuild;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class Guilds {

    public static Set<HolderGuild> SERVERS = new HashSet<HolderGuild>();

    public static HolderGuild registerServer(HolderGuild server) {
        if (!isServerRegistered(server)) {
            SERVERS.add(server);
            return server;
        }

        return getServerFromId(server.getId());
    }

    public static void unregisterServer(HolderGuild server) {
        if (isServerRegistered(server)) SERVERS.remove(server);
    }

    public static boolean isServerRegistered(HolderGuild server) {
        for (HolderGuild s : SERVERS) {
            if (server.equals(s)) return true;
        }

        return false;
    }

    @Nullable
    public static HolderGuild getServerFromId(String guildId) {
        for (HolderGuild server : SERVERS) {
            if (server.getId().equals(guildId)) return server;
        }

        return null;
    }

    @Nullable
    public static HolderGuild getServerFromGuild(Guild guild) {
        return getServerFromId(guild.getId());
    }

}
