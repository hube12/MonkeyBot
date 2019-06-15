package kaptainwutax.monkey.utility;

public class StrUtils {

    public static String removeFirst(String base, String regex) {
        return base.replaceFirst(regex, "");
    }

    public static String removeFirstTrim(String base, String regex) {
        return removeFirst(base, regex).trim();
    }

    public static String getChannelId(String channel) {
        channel = getChannelIdAsMessage(channel);
        return channel.substring(2).replaceFirst(">", "");
    }

    public static String getChannelIdAsMessage(String channel) {
        channel = channel.trim();

        if(channel.startsWith("<#") && channel.endsWith(">")) {
            return channel;
        } else {
            return "<#" + channel + ">";
        }
    }

}
