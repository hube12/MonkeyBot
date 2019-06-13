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
import java.util.ArrayList;
import java.util.List;

public class MonkeyBot extends ListenerAdapter {

    private static MonkeyBot instance;
    public MonkeyConfig config;

    public MonkeyBot(MonkeyConfig config) {
        this.config = config;
    }

    public static MonkeyBot instance() {
        return instance;
    }

    public static void main(String[] args) throws LoginException {
        MonkeyConfig config;
        try {
            config = new Gson().fromJson(new FileReader("config.json"), MonkeyConfig.class);
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't find config file");
            return;
        }

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.build();

        Commands.registerCommands();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) {
           return;
        }

        new Thread() {
            @Override
            public void run() {
                if(Commands.MONKEY.isCommand(event.getMessage().getContentRaw())) {
                    Commands.MONKEY.processCommand(event, event.getMessage().getContentRaw());
                }
            }
        }.start();
    }

}
