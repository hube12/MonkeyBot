package kaptainwutax.monkey.init;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class Exceptions {

    public static SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Not in a guild"));
    public static DynamicCommandExceptionType NO_SUCH_CHANNEL = new DynamicCommandExceptionType(arg -> new LiteralMessage("No such channel: " + arg));
    public static DynamicCommandExceptionType CHANNEL_AMBIGUOUS_EXCEPTION = new DynamicCommandExceptionType(arg -> new LiteralMessage("Channel ambiguous: " + arg));
    public static DynamicCommandExceptionType NO_SUCH_ROLE = new DynamicCommandExceptionType(arg -> new LiteralMessage("No such role: " + arg));
    public static DynamicCommandExceptionType ROLE_AMBIGUOUS_EXCEPTION = new DynamicCommandExceptionType(arg -> new LiteralMessage("Role ambiguous: " + arg));
    public static DynamicCommandExceptionType NO_SUCH_USER = new DynamicCommandExceptionType(arg -> new LiteralMessage("No such user: " + arg));
    public static DynamicCommandExceptionType USER_AMBIGUOUS_EXCEPTION = new DynamicCommandExceptionType(arg -> new LiteralMessage("User ambiguous: " + arg));
    public static DynamicCommandExceptionType NO_SUCH_GUILD = new DynamicCommandExceptionType(arg -> new LiteralMessage("No such guild: " + arg));
    public static DynamicCommandExceptionType GUILD_AMBIGUOUS_EXCEPTION = new DynamicCommandExceptionType(arg -> new LiteralMessage("Guild ambiguous: " + arg));

}
