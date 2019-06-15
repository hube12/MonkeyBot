package kaptainwutax.monkey.utility;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class MonkeyConfig {

    public String token;

    public static MonkeyConfig generateConfig(String location) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(location), MonkeyConfig.class);
    }

}
