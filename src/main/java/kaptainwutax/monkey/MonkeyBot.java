package kaptainwutax.monkey;

import kaptainwutax.monkey.holder.HolderGuild;
import kaptainwutax.monkey.init.Commands;
import kaptainwutax.monkey.init.Guilds;
import kaptainwutax.monkey.utility.Log;
import kaptainwutax.monkey.utility.MonkeyConfig;
import kaptainwutax.monkey.utility.StrUtils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(monkeyBot.config.token);
        builder.addEventListeners(instance());
        monkeyBot.jda = builder.build();

        Commands.registerCommands();
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
        String messageContent = event.getMessage().getContentRaw();

        if(event.getMember().getIdLong() != 572817483489214475L) {
            HolderGuild server = Guilds.instance().getOrCreateServer(new HolderGuild(event.getGuild()));
            server.controller.sanitize(event);
        }

        if(Commands.MONKEY.isCommand(messageContent)) {
            Commands.MONKEY.processCommand(event, messageContent);
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
