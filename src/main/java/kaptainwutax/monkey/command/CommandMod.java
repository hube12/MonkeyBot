package kaptainwutax.monkey.command;

import com.mojang.brigadier.CommandDispatcher;
import kaptainwutax.monkey.MonkeyBot;
import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.holder.UserInfo;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mojang.brigadier.arguments.BoolArgumentType.*;
import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static com.mojang.brigadier.arguments.LongArgumentType.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static kaptainwutax.monkey.command.arguments.ChannelArgumentType.*;
import static kaptainwutax.monkey.command.arguments.RoleArgumentType.*;
import static kaptainwutax.monkey.init.Commands.*;

public class CommandMod {

    public static void register(CommandDispatcher<MessageCommandSource> dispatcher) {
        dispatcher.register(literal("mod", "Moderation tools for Monkey Bot.")
            .requires(MessageCommandSource::isAdministrator)
            .then(literal("setChannel", "Sets the moderation channel. Will be used to print logs.")
                .then(argument("channel", channel())
                    .executes(ctx -> setChannel(ctx.getSource(), getChannel(ctx, "channel")))))
            .then(literal("autoban", "Should the user be automatically banned if he spams? Note that you will always be notified in the moderation channel if a user spams.")
                .requires(CommandMod::hasModerationChannel)
                .then(argument("value", bool())
                    .executes(ctx -> setAutoban(ctx.getSource(), getBool(ctx, "value")))))
            .then(literal("banMessage", "If on, this will print a custom ban message in the target channel whenever someone gets banned from spamming.")
                .requires(CommandMod::hasModerationChannel)
                .then(argument("value", bool())
                    .executes(ctx -> setBanMessage(ctx.getSource(), getBool(ctx, "value")))))
            .then(literal("sendAlert", "If on, spam alerts will be sent to and from other discords.")
                .requires(CommandMod::hasModerationChannel)
                .then(argument("value", bool())
                    .executes(ctx -> setSendAlert(ctx.getSource(), getBool(ctx, "value")))))
            .then(literal("funCommands", "If on, general fun commands will be enabled. Note that some of them are trollish and may cause issues.")
                .requires(CommandMod::hasModerationChannel)
                .then(argument("value", bool())
                    .executes(ctx -> setFunCommands(ctx.getSource(), getBool(ctx, "value")))))
            .then(literal("limit", "Sets the limit of everyone, here, role and user pings for users with the specified role. If the role is unspecified, this applies to users without a role.")
                .requires(CommandMod::hasModerationChannel)
                .then(argument("everyoneLimit", integer(-1))
                    .then(argument("hereLimit", integer(-1))
                        .then(argument("roleLimit", integer(-1))
                            .then(argument("userLimit", integer(-1))
                                .executes(ctx -> setLimits(ctx.getSource(), null, getInteger(ctx, "everyoneLimit"), getInteger(ctx, "hereLimit"), getInteger(ctx, "roleLimit"), getInteger(ctx, "userLimit")))
                                .then(argument("role", role())
                                    .executes(ctx -> setLimits(ctx.getSource(), getRole(ctx, "role"), getInteger(ctx, "everyoneLimit"), getInteger(ctx, "hereLimit"), getInteger(ctx, "roleLimit"), getInteger(ctx, "userLimit")))))))))
            .then(literal("yunDefense", "If on, I'm jokes will be made against Yun. BRING HIM DOWN!")
                .requires(CommandMod::hasModerationChannel)
                .then(argument("value", bool())
                    .executes(ctx -> setYunDefense(ctx.getSource(), getBool(ctx, "value")))))
            .then(literal("unautoban", "Reverse a Monkey autoban. Can only unban users banned via a Monkey autoban.")
                .requires(source -> MonkeyBot.instance().config.botAdmins.contains(source.getUser().getId()))
                .then(argument("userId", longArg())
                    .executes(ctx -> unautoban(ctx.getSource(), getLong(ctx, "userId"), null))
                    .then(argument("reason", greedyString())
                        .executes(ctx -> unautoban(ctx.getSource(), getLong(ctx, "userId"), getString(ctx, "reason"))))))
            .then(literal("broadcast", "Broadcast a message to all moderation channels on all active discords. Use sparingly.")
                .then(argument("message", greedyString())
                    .executes(ctx -> broadcast(ctx.getSource(), getString(ctx, "message"))))));
    }

    private static boolean hasModerationChannel(MessageCommandSource source) {
        if (source.isDMs())
            return false;
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));
        return server.controller.moderationChannel != null && !server.controller.moderationChannel.isEmpty() && source.getGuild().getTextChannelById(server.controller.moderationChannel) != null;
    }

    private static void sendModerationFeedback(MessageCommandSource source, String message) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        TextChannel moderationChannel = source.getGuild().getTextChannelById(StrUtils.getChannelId(server.controller.moderationChannel));
        assert moderationChannel != null;

        source.getChannel().sendMessage(message).queue();
        if (moderationChannel.getIdLong() != source.getChannel().getIdLong())
            moderationChannel.sendMessage(message).queue();
    }

    private static int setChannel(MessageCommandSource source, MessageChannel channel) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.moderationChannel = channel.getId();
        if (source.getChannel().getIdLong() != channel.getIdLong())
            source.getChannel().sendMessage("That channel was successfully set for moderation.").queue();
        channel.sendMessage("This channel was successfully set for moderation.").queue();

        return 0;
    }

    private static int setAutoban(MessageCommandSource source, boolean value) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.autoban = value;
        sendModerationFeedback(source, "Autoban has been updated to " + value);

        return 0;
    }

    private static int setBanMessage(MessageCommandSource source, boolean value) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.banMessage = value;
        sendModerationFeedback(source, value ? "Public ban messages will be displayed." : "Public ban messages will not be displayed.");

        return 0;
    }

    private static int setSendAlert(MessageCommandSource source, boolean value) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.sendAlert = value;
        if (value)
            sendModerationFeedback(source, "This server will be sending and receiving spam alerts from other discords.");
        else
            sendModerationFeedback(source, "This server won't be sending nor receiving spam alerts from other discords.");

        return 0;
    }

    private static int setFunCommands(MessageCommandSource source, boolean value) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.funCommands = value;
        sendModerationFeedback(source, value ? "Enabled fun commands." : "Disabled fun commands.");

        return 0;
    }

    private static int setLimits(MessageCommandSource source, @Nullable Role role, int everyoneLimit, int hereLimit, int roleLimit, int userLimit) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.setRole(role, new int[] {everyoneLimit, hereLimit, roleLimit, userLimit});
        sendModerationFeedback(source, "Updated role limits successfully.");

        return 0;
    }

    private static int setYunDefense(MessageCommandSource source, boolean value) {
        assert source.getGuild() != null;
        HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(source.getGuild()));

        server.controller.yunDefense = value;
        sendModerationFeedback(source, value ? "Enabled Yun defense." : "Disabled Yun defense.");

        return 0;
    }

    private static int unautoban(MessageCommandSource source, long userId, @Nullable String reason) {
        UserInfo user = MonkeyBot.instance().config.getOrCreateUser(userId);
        if (user.autobannedServers.isEmpty()) {
            source.getChannel().sendMessage("That user wasn't autobanned by Monkey").queue();
            return 0;
        }

        AtomicBoolean hasApologized = new AtomicBoolean(false);

        for (String serverId : user.autobannedServers) {
            Guild guild = MonkeyBot.instance().jda.getGuildById(serverId);
            if (guild == null) continue;

            guild.retrieveBanById(user.id).queue(ban -> {
                guild.getController().unban(ban.getUser()).queue();

                if (!hasApologized.getAndSet(true)) {
                    ban.getUser().openPrivateChannel().queue(dms -> {
                        dms.sendMessage("We realize our mistake, and have un-autobanned you from " + user.autobannedServers.size() + " servers. Sorry about that!").queue();
                    }, t -> {});
                }

                HolderGuild holder = Guilds.instance().getOrCreateServer(new HolderGuild(guild));
                if (holder.controller.moderationChannel != null && !holder.controller.moderationChannel.isEmpty()) {
                    TextChannel moderationChannel = guild.getTextChannelById(holder.controller.moderationChannel);
                    if (moderationChannel != null) {
                        assert source.getGuild() != null;
                        moderationChannel.sendMessage(
                                String.format("User %s#%s was un-autobanned by %s in %s%s",
                                        ban.getUser().getName(), ban.getUser().getDiscriminator(),
                                        source.getUser().getName(),
                                        source.getGuild().getName(),
                                        reason == null ? "" : (". Reason: " + reason)))
                                .queue();
                    }
                }
            }, t -> {});
        }

        int count = user.autobannedServers.size();
        user.autobannedServers.clear();
        return count;
    }

    private static int broadcast(MessageCommandSource source, String message) {
        assert source.getGuild() != null;

        for(HolderGuild s: Guilds.instance().servers) {
            if(s.controller.moderationChannel != null && s.controller.sendAlert) {
                TextChannel moderationChannel = s.getGuild().getTextChannelById(StrUtils.getChannelId(s.controller.moderationChannel));

                if(moderationChannel != null) {
                    String broadcastMessage = "====== **[BROADCAST]** ";
                    broadcastMessage += "<@" + source.getUser().getIdLong() + ">";
                    broadcastMessage += " from **" + source.getGuild().getName() + "** ======\n";
                    broadcastMessage += message;
                    Log.print(moderationChannel, broadcastMessage);
                }
            }
        }

        return 0;
    }

}
