package kaptainwutax.monkey;

import com.google.gson.Gson;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.utility.MonkeyConfig;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MonkeyBot extends ListenerAdapter {

    private static MonkeyBot instance;
    public MonkeyConfig config;

    public MonkeyBot() {
    }

    public static MonkeyBot instance() {
        if (instance == null) instance = new MonkeyBot();
        return instance;
    }

    public static void main(String[] args) throws LoginException {
        MonkeyBot monkeyBot = instance();

        try {monkeyBot.config = MonkeyConfig.generateConfig("config.json");}
        catch (FileNotFoundException e) {
            System.err.println("Couldn't find config file.");
            return;
        }

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(monkeyBot.config.token);
        builder.addEventListeners(instance());
        builder.build();

        Commands.registerCommands();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String messageContent = event.getMessage().getContentRaw();
        if (Commands.MONKEY.isCommand(messageContent) || Commands.MONKEY2.isCommand(messageContent)) {
            Commands.MONKEY.processCommand(event, messageContent);
        }
    }

}
