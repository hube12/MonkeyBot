package kaptainwutax.monkey.utility.math;

import kaptainwutax.monkey.command.IExecutable;

import java.util.Objects;

public class LCG implements IExecutable {

    public final long multiplier;
    public final long addend;
    public final long modulo;

    private final boolean hasModulo;
    private final boolean canMask;

    public LCG(long multiplier, long addend) {
        this.multiplier = multiplier;
        this.addend = addend;
        this.modulo = 0;

        this.hasModulo = false;
        this.canMask = true;
    }

    public LCG(long multiplier, long addend, long modulo) {
        this.multiplier = multiplier;
        this.addend = addend;
        this.modulo = modulo;

        this.hasModulo = true;
        this.canMask = (this.modulo & -this.modulo) == this.modulo;
    }

    public long nextSeed(long seed) {
        if(!this.hasModulo) {
            return seed * this.multiplier + this.addend;
        }

        if(this.canMask) {
            return (seed * this.multiplier + this.addend) & (this.modulo - 1);
        }

        return (seed * this.multiplier + this.addend) % this.modulo;
    }

    public LCG combine(long steps) {
        long multiplier = 1;
        long addend = 0;

        long intermediateMultiplier = this.multiplier;
        long intermediateAddend = this.addend;

        for(long k = steps; k != 0; k >>>= 1) {
            if((k & 1) != 0) {
                multiplier *= intermediateMultiplier;
                addend = intermediateMultiplier * addend + intermediateAddend;
            }

            intermediateAddend = (intermediateMultiplier + 1) * intermediateAddend;
            intermediateMultiplier *= intermediateMultiplier;
        }

        if(this.canMask) {
            multiplier &= (this.modulo - 1);
            addend &= (this.modulo - 1);
        } else {
            multiplier %= this.modulo;
            addend %= this.modulo;
        }

        return new LCG(multiplier, addend, this.modulo);
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(!(obj instanceof LCG))return false;
        LCG lcg = (LCG)obj;

        if(this.hasModulo != lcg.hasModulo)return false;
        return this.multiplier == lcg.multiplier && this.addend == lcg.addend && this.modulo == lcg.modulo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.multiplier, this.addend, this.modulo);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LCG{");
        sb.append("multiplier=").append(this.multiplier);
        sb.append(", addend=").append(this.addend);
        if(this.hasModulo)sb.append(", modulo=").append(this.modulo);
        sb.append(", canMask=").append(this.canMask);
        sb.append('}');
        return sb.toString();
    }

    public String toPrettyString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Multiplier: ").append(String.format("0x%X (%d)", this.multiplier, this.multiplier));
        sb.append(", Addend: ").append(String.format("0x%X (%d)", this.addend, this.addend));
        if(this.hasModulo)sb.append(", Modulo: ").append(String.format("0x%X (%d)", this.modulo, this.modulo));
        return sb.toString();
    }

    @Override
    public IExecutable callConstructor(String[] params) {
        try {
            if(params.length == 2) {
                return new LCG(Long.parseLong(params[0]), Long.parseLong(params[1]));
            } else if(params.length == 3) {
                return new LCG(Long.parseLong(params[0]), Long.parseLong(params[1]), Long.parseLong(params[2]));
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
            if(call.equalsIgnoreCase("nextSeed")) {
                if(params.length != 1)return null;
                return this.nextSeed(Long.parseLong(params[0]));
            } else if(call.equalsIgnoreCase("combine")) {
                if(params.length != 1)return null;
                return this.combine(Long.parseLong(params[0]));
            } else if(call.equalsIgnoreCase("toPrettyString")) {
                if(params.length != 0)return null;
                return this.toPrettyString();
            } else {
                return null;
            }
        } catch(Exception e) {
            return null;
        }
    }

}
