package kaptainwutax.monkey;

import kaptainwutax.monkey.command.CommandSummary;
import kaptainwutax.monkey.init.Commands;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class MonkeyBot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "You wish...";
        builder.setToken(token);
        builder.addEventListeners(new MonkeyBot());
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
