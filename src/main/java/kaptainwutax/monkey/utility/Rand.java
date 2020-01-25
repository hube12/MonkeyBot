package kaptainwutax.monkey.utility;

import kaptainwutax.monkey.command.IExecutable;
import kaptainwutax.monkey.utility.math.LCG;

public class Rand implements Cloneable, IExecutable {

    public static final LCG JAVA_LCG = new LCG(0x5DEECE66DL, 0xBL, 1L << 48);
    public static final LCG SU_MLCG = new LCG(181783497276652981L, 0L);

    private static long SU_SEED = 8682522807148012L;
    private long seed;

    public Rand() {
        this.setSeed(System.nanoTime() ^ (SU_SEED = SU_MLCG.nextSeed(SU_SEED)), true);
    }

    public Rand(long seed) {
        this.setSeed(seed, true);
    }

    public Rand(long seed, boolean scramble) {
        this.setSeed(seed, scramble);
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed, boolean scramble) {
        this.seed = seed ^ (scramble ? JAVA_LCG.multiplier : 0L);
        this.seed &= JAVA_LCG.modulo - 1;
    }

    public int next(int bits) {
        this.seed = JAVA_LCG.nextSeed(this.seed);
        return (int)(this.seed >>> (48 - bits));
    }

    public boolean nextBoolean() {
        return this.next(1) == 1;
    }

    public int nextInt() {
        return this.next(32);
    }

    public int nextInt(int bound) {
        if(bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        if((bound & -bound) == bound) {
            return (int)((bound * (long)this.next(31)) >> 31);
        }

        int bits, value;

        do {
            bits = this.next(31);
            value = bits % bound;
        } while(bits - value + (bound - 1) < 0);

        return value;
    }

    public float nextFloat() {
        return this.next(24) / ((float)(1 << 24));
    }

    public long nextLong() {
        return ((long)(this.next(32)) << 32) + this.next(32);
    }

    public double nextDouble() {
        return (((long)next(26) << 27) + next(27)) / (double)(1L << 53);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(!(obj instanceof Rand))return false;
        Rand rand = (Rand)obj;
        return rand.getSeed() == this.getSeed();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Rand{");
        sb.append("seed=").append(seed);
        sb.append('}');
        return sb.toString();
    }

    @Override
    protected Object clone() {
        Rand rand = new Rand(this.getSeed(), false);
        return rand;
    }

    @Override
    public IExecutable callConstructor(String[] params) {
        System.out.println(params.length);
        try {
            if(params.length == 0) {
                return new Rand();
            } else if(params.length == 1) {
                return new Rand(Long.parseLong(params[0]));
            } else if(params.length == 2) {
                return new Rand(Long.parseLong(params[0]), Boolean.parseBoolean(params[1]));
            } else {
                return null;
            }
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public Object callMethod(String call, String[] params) {
        try {
            if(call.equalsIgnoreCase("getSeed")) {
                if(params.length != 0)return null;
                return this.getSeed();
            } else if(call.equalsIgnoreCase("JAVA_LCG")) {
                if(params.length != 0)return null;
                return JAVA_LCG;
            } else if(call.equalsIgnoreCase("SU_MLCG")) {
                if(params.length != 0)return null;
                return SU_MLCG;
            } else if(call.equalsIgnoreCase("next")) {
                if(params.length != 1)return null;
                return this.next(Integer.parseInt(params[0]));
            } else if(call.equalsIgnoreCase("nextBoolean")) {
                if(params.length != 0)return null;
                return this.nextBoolean();
            } else if(call.equalsIgnoreCase("nextInt")) {
                if(params.length == 0)return this.nextInt();
                else if(params.length == 1)return this.nextInt(Integer.parseInt(params[0]));
                else return null;
            } else if(call.equalsIgnoreCase("nextFloat")) {
                if(params.length != 0)return null;
                return this.nextFloat();
            } else if(call.equalsIgnoreCase("nextLong")) {
                if(params.length != 0)return null;
                return this.nextLong();
            } else if(call.equalsIgnoreCase("nextDouble")) {
                if(params.length != 0)return null;
                return this.nextDouble();
            } else {
                return null;
            }
        } catch(Exception e) {
                return null;
        }
    }

}

