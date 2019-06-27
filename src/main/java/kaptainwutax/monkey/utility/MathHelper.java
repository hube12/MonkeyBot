package kaptainwutax.monkey.utility;

public class MathHelper {

    public static int clamp(int n, int min, int max) {
        if(n < min)return min;
        return n > max ? max : n;
    }

}
