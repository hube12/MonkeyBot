package kaptainwutax.monkey.holder;

import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.MessageLimiter;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HolderController {

    private HolderGuild server;
    public String moderationChannel = null;
    public boolean autoban = false;

    private MessageLimiter NOOB_LIMIT = new MessageLimiter(0, 0, 0,0);
    private List<RoleMap> LIMITS = new ArrayList<RoleMap>();

    public HolderController(HolderGuild server) {
        this.server = server;
    }

    public void sanitize(MessageReceivedEvent event) {
        if(this.moderationChannel == null || !this.isConsideredSpam(event))return;

        this.attemptBan(event, false);

        for(HolderGuild s: Guilds.instance().servers) {
            if(!s.equals(this.server) && s.controller.moderationChannel != null) {
                TextChannel moderationChannel = s.guild.getTextChannelById(this.moderationChannel);
                Log.print(moderationChannel, "Spam alert from **" + event.getGuild().getName() + "**. User <@" + event.getMember().getIdLong() + "> has been spamming pings.");

                if(s.controller.autoban) {
                    Log.print(moderationChannel, "Banned <@" + event.getMember().getIdLong() + ">, please double check to make sure it wasn't a mistake.");
                    //server.guild.getController().ban(event.getMember(), 0, "Automatic ping ban from " + event.getGuild().getName() + ".");
                }
            }
        }
    }

    private void attemptBan(MessageReceivedEvent event, boolean force) {
        TextChannel moderationChannel = this.server.guild.getTextChannelById(StrUtils.getChannelId(this.moderationChannel));
        Log.print(moderationChannel, "Member <@" + event.getMember().getIdLong() + "> is spamming pings in " + StrUtils.getChannelIdAsMessage(event.getChannel().getId()) + ".");
        if(!force && !this.autoban)return;
        Log.print(moderationChannel, "Banned <@" + event.getMember().getIdLong() + ">, please double check to make sure it wasn't a mistake.");
        //server.guild.getController().ban(event.getMember(), 0, "Automatic ping ban.");
    }

    public boolean isConsideredSpam(MessageReceivedEvent event) {
        MessageLimiter messageMentions = new MessageLimiter(this.getMentions(event));
        return !messageMentions.respectsLimits(this.getLimit(event.getMember()));
    }

    public MessageLimiter getLimit(Member member) {
        List<Role> roles = member.getRoles();

        if(roles.isEmpty()) {
            return this.NOOB_LIMIT;
        } else {
            Role role = roles.get(0);

            for(RoleMap roleMap: LIMITS) {
                if(roleMap.id == role.getIdLong())return roleMap.getValue();
            }

            RoleMap roleMap1 = new RoleMap(role.getIdLong());
            LIMITS.add(roleMap1);
            return roleMap1.getValue();
        }
    }

    private int[] getMentions(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        messageContent = removeHiddenText(messageContent, "```");
        messageContent = removeHiddenText(messageContent, "`");

        int everyone = findSimplePing(messageContent, "@everyone");
        int here = findSimplePing(messageContent, "@here");
        int role = event.getMessage().getMentionedRoles().size();
        int user = event.getMessage().getMentionedMembers().size();

        return new int[] {everyone, here, role, user};
    }

    private String removeHiddenText(String s, String brackets) {
        String finalText = s.trim();
        s = s.trim();

        int start = -1;

        for(int i = 0; i < s.length(); i++) {
            String c = new String();

            for(int j = i; j < s.length() && j < i + brackets.length(); j++) {
                c += s.charAt(j);
            }

            if(c.equals(brackets)) {
                if(start == -1) {
                    start = i;
                } else {
                    finalText = StrUtils.removeFirst(finalText, s.substring(start, i + brackets.length()));
                    start = -1;
                }
            }
        }

        return finalText;
    }

    private int findSimplePing(String rawText, String ping) {
        int count = 0;

        while(rawText.contains(ping)) {
            rawText = StrUtils.removeFirst(rawText, ping);
            count++;
        }

        return count;
    }

    private class RoleMap {

        long id;
        MessageLimiter limiter;

        public RoleMap(long id) {
            this.id = id;
        }

        public long getId() {
            return this.id;
        }

        public RoleMap setValue(MessageLimiter limiter) {
            this.limiter = limiter;
            return this;
        }

        public MessageLimiter getValue() {
            if(limiter == null)limiter = new MessageLimiter(0, 0, 0, 0);
            return this.limiter;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null) return false;
            if (obj.getClass() != HolderController.class) return false;
            RoleMap target = ((RoleMap) obj);
            return target.getId() == this.id;
        }
    }

}
