package kaptainwutax.monkey.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kaptainwutax.monkey.command.Exceptions;
import kaptainwutax.monkey.command.MessageCommandSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RoleArgumentType implements ArgumentType<RoleArgumentType.RoleGetter> {

    private static final Collection<String> EXAMPLES = Arrays.asList("word", "123", "<@&role>");

    private RoleArgumentType() {}

    public static RoleArgumentType role() {
        return new RoleArgumentType();
    }

    public static <S> RoleGetter getRoleGetter(CommandContext<S> context, String argName) {
        return context.getArgument(argName, RoleGetter.class);
    }

    public static Role getRole(CommandContext<MessageCommandSource> context, String argName) throws CommandSyntaxException {
        return getRoleGetter(context, argName).getRole(context.getSource());
    }

    @Override
    public RoleGetter parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '<') {
            reader.skip();
            reader.expect('@');
            reader.expect('&');
            long roleId = reader.readLong();
            reader.expect('>');
            return source -> getRoleById(source, roleId);
        } else {
            int start = reader.getCursor();
            try {
                long roleId = reader.readLong();
                return source -> getRoleById(source, roleId);
            } catch (CommandSyntaxException e) {
                reader.setCursor(start);
                String roleName = reader.readString();
                return source -> getRoleByName(source, roleName);
            }
        }
    }

    private static Role getRoleById(MessageCommandSource source, long roleId) throws CommandSyntaxException {
        if (source.isDMs())
            throw Exceptions.NOT_IN_GUILD.create();
        Guild guild = source.getGuild();
        assert guild != null;
        Role role = guild.getRoleById(roleId);
        if (role == null)
            throw Exceptions.NO_SUCH_ROLE.create(roleId);
        return role;
    }

    private static Role getRoleByName(MessageCommandSource source, String roleName) throws CommandSyntaxException {
        if (source.isDMs())
            throw Exceptions.NOT_IN_GUILD.create();
        Guild guild = source.getGuild();
        assert guild != null;
        List<Role> roles = guild.getRolesByName(roleName, true);
        if (roles.isEmpty())
            throw Exceptions.NO_SUCH_ROLE.create(roleName);
        if (roles.size() == 1)
            return roles.get(0);
        roles = guild.getRolesByName(roleName, false);
        if (roles.size() != 1)
            throw Exceptions.ROLE_AMBIGUOUS_EXCEPTION.create(roleName);
        return roles.get(0);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public interface RoleGetter {
        Role getRole(MessageCommandSource source) throws CommandSyntaxException;
    }
}
