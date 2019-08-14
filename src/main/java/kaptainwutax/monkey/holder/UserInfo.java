package kaptainwutax.monkey.holder;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

    /**
     * The user ID
     */
    @Expose public long id;

    /**
     * The servers IDs from which this user has been autobanned
     */
    @Expose public List<String> autobannedServers = new ArrayList<>();

    private UserInfo() {} // for GSON

    public UserInfo(long id) {
        this.id = id;
    }

}
