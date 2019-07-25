package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandMod extends Command {

    public CommandMod(String[] prefix) {
        super(prefix);
    }

    @Override
    public void processCommand(MessageReceivedEvent message, String rawCommand) {
        rawCommand = this.removePrefix(rawCommand);

        if(!message.getMember().hasPermission(Permission.ADMINISTRATOR))return;

        if(rawCommand.startsWith("setChannel")) {
            this.setChannel(message, StrUtils.removeFirstTrim(rawCommand, "setChannel"));
        } else if(rawCommand.startsWith("autoban")) {
            this.setAutoban(message, StrUtils.removeFirstTrim(rawCommand, "autoban"));
        }
    }

    private void setChannel(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        String channelId = StrUtils.getChannelId(params);
        TextChannel channel = message.getGuild().getTextChannelById(StrUtils.getChannelId(channelId));

        if(channel == null) {
            Log.print(message.getTextChannel(), "There was an error accessing this channel.");
        } else {
            Log.print(channel, "This channel was successfully set for moderation.");
            server.controller.moderationChannel = channelId;
        }
    }

    private void setAutoban(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(server.controller.moderationChannel == null) {
            Log.print(message.getTextChannel(), "This command requires a moderation channel. Use [monkey mod setChannel <#channel>].");
        }

        TextChannel moderationChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(server.controller.moderationChannel));

        params = params.toLowerCase();

        if(params.equals("false")) {
            server.controller.autoban = false;
            Log.print(moderationChannel, "Autoban has been updated to false.");
        } else if(params.equals("true")) {
            server.controller.autoban = true;
            Log.print(moderationChannel, "Autoban has been updated to true.");
        } else {
            Log.print(moderationChannel, "Unknown argument \"" + params + "\".");
        }
    }

    private void setLimit(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        List<String> rawLimits = Arrays.asList(params.split(" "));

        if(rawLimits.size() != 5) {
            Log.print(message.getTextChannel(), "Invalid command arguments.");
            return;
        }

        String roleId = rawLimits.get(0);
        rawLimits.remove(0);

        int[] limits = rawLimits.stream().mapToInt(Integer::parseInt).toArray();

        Role role = server.guild.getRoleById(roleId.replaceFirst("<@&", "").replaceFirst(">", ""));

        if(role == null) {
            Log.print(message.getTextChannel(), "The role you specified is invalid.");
            return;
        }

        server.controller.setRole(role, limits);
    }

    @Override
    public String[] getCommandDesc() {
        return new String[0];
    }

}
