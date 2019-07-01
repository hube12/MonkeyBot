package kaptainwutax.monkey.utility;

import java.util.ArrayList;
import java.util.List;

public class StrUtils {

    public static final int MESSAGE_LIMIT = 2000;

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

    public static String[] splitMessage(String message) {
        // fast exit for the trivial case
        if (message.length() <= MESSAGE_LIMIT) return new String[] {message};

        String[] lines = message.split("\n");

        List<String> newMessages = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();

        String code = null;

        for (String line : lines) {
            int newLength = currentMessage.length() + line.length();
            if (currentMessage.length() != 0)
                newLength++; // newline

            if (line.startsWith("```")) {
                if (code == null) code = line.substring(3);
                else code = null;
            }

            final int limit = code == null ? MESSAGE_LIMIT : MESSAGE_LIMIT - 4;

            if (newLength > limit) {
                if (code != null)
                    currentMessage.append("\n```");
                newMessages.add(currentMessage.toString());
                currentMessage = new StringBuilder();
                if (code != null)
                    currentMessage.append("```").append(code);
            }

            if (currentMessage.length() != 0)
                currentMessage.append("\n");
            currentMessage.append(line);
        }

        newMessages.add(currentMessage.toString());

        return newMessages.toArray(new String[0]);
    }

}
