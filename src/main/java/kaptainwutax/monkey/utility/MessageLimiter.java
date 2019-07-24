package kaptainwutax.monkey.utility;

public class MessageLimiter {

    public int everyone;
    public int here;
    public int role;
    public int user;

    public MessageLimiter(int everyone, int here, int role, int user) {
        this.everyone = everyone;
        this.here = here;
        this.role = role;
        this.user = user;
    }

    public MessageLimiter(int[] data) {
        this.everyone = data[0];
        this.here = data[1];
        this.role = data[2];
        this.user = data[3];
    }

    public boolean respectsLimits(MessageLimiter limiter) {
        if(this.everyone > limiter.everyone)return false;
        else if(this.here > limiter.here)return false;
        else if(this.role > limiter.role)return false;
        else if(this.user > limiter.user)return false;

        return true;
    }

}
