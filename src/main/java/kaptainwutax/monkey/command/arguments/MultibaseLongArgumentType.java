package kaptainwutax.monkey.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class MultibaseLongArgumentType implements ArgumentType<Long> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123", "0xf00baa", "0xF00BAA", "0123", "0b101");

    private final long minimum;
    private final long maximum;

    private MultibaseLongArgumentType(final long minimum, final long maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static MultibaseLongArgumentType multibaseLong() {
        return multibaseLong(Long.MIN_VALUE);
    }

    public static MultibaseLongArgumentType multibaseLong(final long min) {
        return multibaseLong(min, Long.MAX_VALUE);
    }

    public static MultibaseLongArgumentType multibaseLong(final long min, final long max) {
        return new MultibaseLongArgumentType(min, max);
    }

    public static long getMultibaseLong(final CommandContext<?> context, final String name) {
        return context.getArgument(name, long.class);
    }

    public long getMinimum() {
        return minimum;
    }

    public long getMaximum() {
        return maximum;
    }

    @Override
    public Long parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final long result;
        if (reader.peek() == '0') {
            reader.skip();
            if (!reader.canRead()) {
                result = 0;
            } else if (reader.peek() == 'x' || reader.peek() == 'X') {
                reader.skip();
                int nStart = reader.getCursor();
                while (reader.canRead() && ((reader.peek() >= '0' && reader.peek() <= '9') || (reader.peek() >= 'a' && reader.peek() <= 'f') || (reader.peek() >= 'A' && reader.peek() <= 'F')))
                    reader.skip();
                try {
                    result = Long.parseLong(reader.getString().substring(nStart, reader.getCursor()), 16);
                } catch (NumberFormatException e) {
                    reader.setCursor(start);
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(reader);
                }
            } else if (reader.peek() == 'b' || reader.peek() == 'B') {
                reader.skip();
                int nStart = reader.getCursor();
                while (reader.canRead() && (reader.peek() == '0' || reader.peek() == '1'))
                    reader.skip();
                try {
                    result = Long.parseLong(reader.getString().substring(nStart, reader.getCursor()), 2);
                } catch (NumberFormatException e) {
                    reader.setCursor(start);
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(reader);
                }
            } else if (reader.peek() >= '0' && reader.peek() <= '7') {
                int nStart = reader.getCursor();
                while (reader.canRead() && reader.peek() >= '0' && reader.peek() <= '7')
                    reader.skip();
                try {
                    result = Long.parseLong(reader.getString().substring(nStart, reader.getCursor()), 8);
                } catch (NumberFormatException e) {
                    reader.setCursor(start);
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(reader);
                }
            } else {
                reader.setCursor(start);
                result = reader.readLong();
            }
        } else {
            result = reader.readLong();
        }
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MultibaseLongArgumentType)) return false;

        final MultibaseLongArgumentType that = (MultibaseLongArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(minimum) + Long.hashCode(maximum);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
