package kaptainwutax.monkey.command;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
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
        } else if(rawCommand.startsWith("banMessage")) {
            this.setBanMessage(message, StrUtils.removeFirstTrim(rawCommand, "banMessage"));
        } else if(rawCommand.startsWith("sendAlert")) {
            this.setSendAlert(message, StrUtils.removeFirstTrim(rawCommand, "sendAlert"));
        } else if(rawCommand.startsWith("funCommands")) {
            this.setFunCommands(message, StrUtils.removeFirstTrim(rawCommand, "funCommands"));
        } else if(rawCommand.startsWith("limit")) {
            this.setLimit(message, StrUtils.removeFirstTrim(rawCommand, "limit"));
        } else if (rawCommand.startsWith("yunDefense")) {
            this.setYunDefense(message, StrUtils.removeFirstTrim(rawCommand, "yunDefense"));
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

        if(server.controller.moderationChannel == null || server.controller.moderationChannel.isEmpty()) {
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

    private void setBanMessage(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(server.controller.moderationChannel == null || server.controller.moderationChannel.isEmpty()) {
            Log.print(message.getTextChannel(), "This command requires a moderation channel. Use [monkey mod setChannel <#channel>].");
        }

        TextChannel moderationChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(server.controller.moderationChannel));
        params = params.toLowerCase();

        if(params.equals("false")) {
            server.controller.banMessage = false;
            Log.print(moderationChannel, "Public ban messages will not be displayed.");
        } else if(params.equals("true")) {
            server.controller.banMessage = true;
            Log.print(moderationChannel, "Public ban messages will be displayed.");
        } else {
            Log.print(moderationChannel, "Unknown argument \"" + params + "\".");
        }
    }

    private void setSendAlert(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(server.controller.moderationChannel == null || server.controller.moderationChannel.isEmpty()) {
            Log.print(message.getTextChannel(), "This command requires a moderation channel. Use [monkey mod setChannel <#channel>].");
        }

        TextChannel moderationChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(server.controller.moderationChannel));
        params = params.toLowerCase();

        if(params.equals("false")) {
            server.controller.sendAlert = false;
            Log.print(moderationChannel, "This server won't be sending nor getting spam alerts from other discords.");
        } else if(params.equals("true")) {
            server.controller.sendAlert = true;
            Log.print(moderationChannel, "This server will be sending and getting spam alerts from other discords.");
        } else {
            Log.print(moderationChannel, "Unknown argument \"" + params + "\".");
        }
    }

    private void setFunCommands(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(server.controller.moderationChannel == null || server.controller.moderationChannel.isEmpty()) {
            Log.print(message.getTextChannel(), "This command requires a moderation channel. Use [monkey mod setChannel <#channel>].");
        }

        TextChannel moderationChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(server.controller.moderationChannel));
        params = params.toLowerCase();

        if(params.equals("false")) {
            server.controller.funCommands = false;
            Log.print(moderationChannel, "Disabled fun commands.");
        } else if(params.equals("true")) {
            server.controller.funCommands = true;
            Log.print(moderationChannel, "Enabled fun commands.");
        } else {
            Log.print(moderationChannel, "Unknown argument \"" + params + "\".");
        }
    }

    private void setLimit(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        List<String> rawLimits = Arrays.asList(params.split(" "));

        String roleId = null;
        int[] limits;

        if(rawLimits.size() == 4) {
            try {
                limits = rawLimits.stream().mapToInt(Integer::parseInt).toArray();
            } catch(Exception e) {
                Log.print(message.getTextChannel(), "Ping limit must be a valid integer.");
                return;
            }

            server.controller.setRole(null, limits);
        } else if(rawLimits.size() == 5) {
            roleId = rawLimits.get(0);

            try {
                limits = rawLimits.subList(1, rawLimits.size()).stream().mapToInt(Integer::parseInt).toArray();
            } catch(Exception e) {
                Log.print(message.getTextChannel(), "Ping limit must be a valid integer.");
                return;
            }

            Role role = server.getGuild().getRoleById(roleId.replaceFirst("<@&", "").replaceFirst(">", ""));

            if(role == null) {
                Log.print(message.getTextChannel(), "The role you specified is invalid.");
                return;
            }

            server.controller.setRole(role, limits);
        } else {
            Log.print(message.getTextChannel(), "Invalid command arguments.");
            return;
        }

        Log.print(message.getTextChannel(), "Updated role limits successfully.");
    }

    private void setYunDefense(MessageReceivedEvent message, String params) {
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(message.getGuild()));

        if(server.controller.moderationChannel == null || server.controller.moderationChannel.isEmpty()) {
            Log.print(message.getTextChannel(), "This command requires a moderation channel. Use [monkey mod setChannel <#channel>].");
        }

        TextChannel moderationChannel = message.getGuild().getTextChannelById(StrUtils.getChannelId(server.controller.moderationChannel));
        params = params.toLowerCase();

        if(params.equals("false")) {
            server.controller.yunDefense = false;
            Log.print(moderationChannel, "Disabled Yun defense.");
        } else if(params.equals("true")) {
            server.controller.yunDefense = true;
            Log.print(moderationChannel, "Enabled Yun defense.");
        } else {
            Log.print(moderationChannel, "Unknown argument \"" + params + "\".");
        }
    }

    @Override
    public String[] getCommandDesc() {
        return new String[] {
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "setChannel <#channel> ` : Sets the moderation channel. Will be used to print logs.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "autoban <flag> ` : Should the user be automatically banned if he spams? Note that you will always be notified in the moderation channel if a user spams.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "banMessage <flag> ` : If on, this will print a custom ban message in the target channel whenever someone gets banned from spamming.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "sendAlert <flag> ` : If on, spam alerts will be sent to and from other discords.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "funCommands <flag> ` : If on, general fun commands will be enabled. Note that some of them are trollish and may cause issues.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "limit <everyone> <here> <role> <user> ` : Sets the limit of everyone, here, role and user pings for users without a role.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "limit <@&role> <everyone> <here> <role> <user> ` : Sets the limit of everyone, here, role and user pings for the specified role.",
                "`" + Commands.MONKEY.getPrefixDesc() + this.getPrefixDesc() + "yunDefense <flag> ` : If on, I'm jokes will be made against Yun. BRING HIM DOWN!",
        };
    }

}
