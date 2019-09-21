package kaptainwutax.monkey;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kaptainwutax.monkey.command.MessageCommandSource;
import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.MonkeyConfig;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class MonkeyBot extends ListenerAdapter {

    private static MonkeyBot instance;
    public MonkeyConfig config;
    public JDA jda;
    public CommandDispatcher<MessageCommandSource> dispatcher;

    public MonkeyBot() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                MonkeyConfig.saveConfig(this.config, "config.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static MonkeyBot instance() {
        if(instance == null) instance = new MonkeyBot();
        return instance;
    }

    public static void main(String[] args) throws LoginException {
        MonkeyBot monkeyBot = instance();

        try {
            monkeyBot.config = MonkeyConfig.generateConfig("config.json");
        } catch(FileNotFoundException e) {
            System.err.println("Couldn't find config file. Looking for environment variables...");
            monkeyBot.config = new MonkeyConfig();
            monkeyBot.config.token = System.getenv("TOKEN");

            if(monkeyBot.config.token == null) {
                System.err.println("Couldn't find the variable either.");
                return;
            }
        }

        // TODO: Threadsafe autosaves
        /*
        new Thread(() -> {
            while(true) {
                try {
                    MonkeyConfig.saveConfig(monkeyBot.config, "config.json");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(monkeyBot.config.token);
        builder.addEventListeners(instance());
        monkeyBot.jda = builder.build();
        monkeyBot.dispatcher = new CommandDispatcher<>();
        Commands.registerCommands(monkeyBot.dispatcher);
        monkeyBot.dispatcher.findAmbiguities(((parent, child, sibling, inputs) ->
                System.err.println("Ambiguity detected between " + monkeyBot.dispatcher.getPath(child) + " and " + monkeyBot.dispatcher.getPath(sibling) + " for inputs " + inputs)));
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        for(Guild guild: instance().jda.getGuildCache().asList()) {
            Guilds.instance().getOrCreateServer(new HolderGuild(guild));
        }
    }

    public void shutdown() {
        jda.shutdown();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String messageContent = event.getMessage().getContentRaw();

        // Interpret every DM from users as a command, otherwise require prefix
        boolean prefixPresent = false;

        // Remove "monkey" prefix if it is present
        for (String prefix : config.commandPrefix) {
            if (messageContent.startsWith(prefix + " ")) {
                messageContent = messageContent.substring(prefix.length() + 1);
                prefixPresent = true;
                break;
            }
        }

        if (prefixPresent || event.getChannelType() == ChannelType.PRIVATE) {
            try {
                dispatcher.execute(messageContent, new MessageCommandSource(event));
            } catch (CommandSyntaxException e) {
                event.getChannel().sendMessage("Error").queue(msg -> msg.editMessage(e.getMessage()).queue());
            }
        }

        if (event.getChannelType() == ChannelType.TEXT) {
            HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(event.getGuild()));
            if (server != null) server.controller.sanitize(event);
        }
    }

    @Override
    public void onShutdown(@Nonnull ShutdownEvent event) {
        try {
            MonkeyConfig.saveConfig(config, "config.json");
        } catch (IOException e) {
            System.err.println("Failed to save config.");
        }
    }
}
