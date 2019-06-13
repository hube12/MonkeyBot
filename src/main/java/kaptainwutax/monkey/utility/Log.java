package kaptainwutax.monkey.utility;

import net.dv8tion.jda.api.entities.TextChannel;

public class Log {

    public static void print(TextChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public static void delete(TextChannel channel, long messageId) {
        channel.deleteMessageById(messageId).queue();
    }

    public static void edit(TextChannel channel, long messageId, String newMessage) {
        channel.editMessageById(messageId, newMessage).queue();
    }

}
