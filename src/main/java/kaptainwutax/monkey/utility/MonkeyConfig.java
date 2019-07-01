package kaptainwutax.monkey.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kaptainwutax.monkey.init.Guilds;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MonkeyConfig {

    public String token;
    public List<String> botAdmins = new ArrayList<>();
    public Guilds guilds = new Guilds();

    public static MonkeyConfig generateConfig(String location) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(location), MonkeyConfig.class);
    }

    public static void saveConfig(MonkeyConfig config, String location) throws IOException {
        FileWriter writer = new FileWriter(location);
        new GsonBuilder().setPrettyPrinting().create().toJson(config, writer);
        writer.flush();
        writer.close();
    }

}
