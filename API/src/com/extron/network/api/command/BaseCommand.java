package com.extron.network.api.command;

import com.extron.network.api.Main;
import com.extron.network.api.command.argument.*;
import com.extron.network.api.command.exceptions.*;
import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.Pair;
import com.extron.network.api.utils.TextUtils;
import net.minecraft.server.v1_8_R1.INamable;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class BaseCommand implements Command {

	@Override
	public String getName() {
        String name = this.getClass().getSimpleName().toLowerCase();
        String cmd = "command";
        if (name.startsWith("command")) {
            return name.substring(cmd.length());
        } else {
            return name;
        }
    }
	
	/**
     * A {@link Check} array to define a case of checks of a perfect success - no exceptions were thrown, no null was returned and if the return type was boolean, == true
     */
    protected static final Check[] FULL_SUCCESS = new Check[]{Check.TRUE, Check.NO_EXCEPTION, Check.NOT_NULL};
    protected static final Check[] COMPLETE_FAILURE = new Check[]{Check.FALSE, Check.NULL, Check.EXCEPTION};

    /**
     * Will return an {@link Command} by it's name or alias.
     * @param name
     * @return
     */
    protected Command getCommandByName(String name) {
        return Main.getCommandManager().getCommand(name);
    }

    /**
     * Finds a player by his name.
     * @param playerArg The argument of the player name.
     * @return An ExtronPlayer with that name, or null if the argument is optional and unspecified.
     * @throws CommandException if there is no such player, or the argument is invalid.
     */
    protected static ExtronPlayer getPlayerByName(Pair<Argument<?>,String> playerArg) throws CommandException {
        if (checkRequired(playerArg)) throw new InvalidUsageException("Invalid usage!");
        ExtronPlayer p = Main.getPlayer(playerArg.getSecond());
        if (p == null) {
            throw new InvalidArgumentException(playerArg.getFirst(),playerArg.getSecond(),"This %s does not exist!");
        }
        return p;
    }

    /**
     * Finds a online/offline player by his name.
     * @param playerArg The argument of the player name.
     * @param online Should find only online or offline players?
     * @return The ExtronPlayer with that name, or null if the argument is optional and unspecified.
     * @throws CommandException if there is no such online/offline player right now, or the argument is invalid.
     */
    protected static ExtronPlayer getPlayerByName(Pair<Argument<?>,String> playerArg, boolean online) throws CommandException {
        if (checkRequired(playerArg)) return null;
        if (online) {
        	return getOnlinePlayerByName(playerArg);
        } else {
            return getOfflinePlayerByName(playerArg,false);
        }
    }

    private static boolean checkRequired(Pair<Argument<?>, String> arg) throws InvalidUsageException {
        if (arg == null || arg.getFirst() == null) throw new InvalidUsageException("Invalid Usage!");
        if (arg.getSecond() == null && arg.getFirst().isRequired()) throw new InvalidUsageException("Invalid Usage!");
        return arg.getSecond() == null;
    }

    protected static ExtronPlayer getOfflinePlayerByName(Pair<Argument<?>,String> playerArg, boolean mustBeOffline) throws CommandException {
        if (checkRequired(playerArg)) return null;
        ExtronPlayer p = Main.getPlayer(playerArg.getSecond());
        if (p == null) {
            throw new InvalidArgumentException(playerArg.getFirst(),playerArg.getSecond(),"This %s does not exist!");
        } else if (p.isOnline() && mustBeOffline) {
            throw new CommandFail("This " + playerArg.getFirst().getName() + " is online!");
        }
        return p;
    }

    protected static ExtronPlayer getOnlinePlayerByName(Pair<Argument<?>,String> playerArg) throws CommandException {
        if (checkRequired(playerArg)) throw new InvalidUsageException("Invalid Usage!");
        ExtronPlayer p = Main.getOnlinePlayer(playerArg.getSecond());
        if (p == null) {
            throw new InvalidArgumentException(playerArg.getFirst(),playerArg.getSecond(),"This %s does not exist!");
        }
        return p;
    }

    /**
     * Method to parse and return the enum getValue from the enum argument. Note that the argument can also be just a string with valid params matching the enum's values.
     * @param enumArg The enum argument.
     * @param enumType The enum class to parse from.
     * @param <T> The type of the enum.
     * @return The enum instance matching (ignoring case) to the argument getValue.
     * @throws CommandException if there is no enum getValue matching the argument's getValue.
     */
    @Nonnull
    protected static <T extends Enum<T>> T getEnumValue(Pair<Argument<?>,String> enumArg, Class<? extends T> enumType) throws CommandException {
        if (checkRequired(enumArg)) throw new InvalidUsageException("Invalid Usage!");
        for (T e : enumType.getEnumConstants()) {
            if (e instanceof INamable) {
                if (((INamable) e).getName().equalsIgnoreCase(enumArg.getSecond())) {
                    return e;
                }
            } else {
                if (e.toString().equalsIgnoreCase(enumArg.getSecond())) {
                    return e;
                }
            }
        }
        throw new CommandFail("Invalid " + enumArg.getFirst().getName() + " '" + enumArg.getSecond() + "'");
    }


    /**
     *
     * Method to parse and return the enum getValue from the enum argument. Note that the argument can also be just a string with valid params matching the enum's values.
     * @param enumArg The enum argument.
     * @param def The enum class to parse from.
     * @param <T> The default getValue to return if argument is optional and unspecified.
     * @return The enum instance matching (ignoring case) to the argument getValue.
     * @throws CommandException if there is no enum getValue matching the argument's getValue.
     */
    protected static <T extends Enum<T>> T getEnumValue(Pair<Argument<?>,String> enumArg, T def) throws CommandException {
        if (checkRequired(enumArg)) return def;
        for (T e : def.getDeclaringClass().getEnumConstants()) {
            if (e instanceof INamable) {
                if (((INamable) e).getName().equalsIgnoreCase(enumArg.getSecond())) {
                    return e;
                }
            } else {
                if (e.toString().equalsIgnoreCase(enumArg.getSecond())) {
                    return e;
                }
            }
        }
        throw new InvalidArgumentException(enumArg.getFirst(),enumArg.getSecond(),"Invalid %s '%s'");
    }

    protected static void isNull(Object obj, String errorMsg) throws CommandException {
        if (obj == null) {
            throw new CommandFail(errorMsg);
        }
    }

    protected static void isNotNull(Object obj, String errorMsg) throws CommandException {
        if (obj != null) {
            throw new CommandFail(errorMsg);
        }
    }

    protected static void checkRange(int i, int min, int max, String errorMsg) throws CommandException {
        if (i < min || i > max) {
            throw new CommandFail(String.format(errorMsg,min,max));
        }
    }

    protected static boolean getBoolean(Pair<Argument<?>,String> arg, boolean def) throws CommandException {
        if (arg.getSecond() == null) return def;
        if (arg.getSecond().equalsIgnoreCase("true")) {
            return true;
        } else if (arg.getSecond().equalsIgnoreCase("false")) {
            return false;
        }
        throw new InvalidArgumentException(arg.getFirst(),arg.getSecond(),"Invalid boolean %s '%s'. Use only \"true\" / \"false\" " + (arg.getFirst().isRequired() ? "" : " (defaults to " + def + ")"));
    }

    protected static void nullOrSuccess(Object obj, String nullError, String success) throws CommandException {
        if (obj == null) {
            throw new CommandFail(nullError);
        }
        throw new CommandSuccess(success);
    }

    protected static <T> void checkMethod(Supplier<T> method, String success, String error, Check... checks) throws CommandException {
        checkMethod(method,success,error,true,checks);
    }

    protected static <T> T checkMethod(Supplier<T> method, String success, String error, boolean last, Check... checks) throws CommandException {
        List<Check> checkList;
        if (checks == null || checks.length == 0) {
            checkList = Arrays.asList(FULL_SUCCESS);
        } else {
            checkList = Arrays.asList(checks);
        }
        T obj = null;
        try {
            obj = method.get();
            if (obj == null) {
                checkResult(Check.NOT_NULL, checkList);
            } else {
                checkResult(Check.NULL, checkList);
                if (obj instanceof Boolean) {
                    boolean b = (Boolean) obj;
                    if (b) {
                        checkResult(Check.FALSE, checkList);
                    } else {
                        checkResult(Check.TRUE, checkList);
                    }
                }
            }
            checkResult(Check.EXCEPTION, checkList);
        } catch (CommandFail e) {
            throw new CommandFail(error);
        } catch (Exception e) {
            try {
                checkResult(Check.NO_EXCEPTION, checkList);
            } catch (CommandFail e2) {
                throw new CommandFail(error);
            }
        }
        if (last) {
            throw new CommandSuccess(success);
        }
        return obj;
    }

    private static void checkResult(Check check, List<Check> checkList) throws CommandException {
        if (checkList.contains(check)) {
            throw new CommandFail("");
        }
    }

    protected static CommandManager getCommandManager() {
        return Main.getCommandManager();
    }

    protected enum Check {
        NULL, NOT_NULL, TRUE, FALSE, EXCEPTION, NO_EXCEPTION;
    }

    protected static void conditionedNullAndSuccess(Object obj, Runnable notNull, String success, String objectNull) throws CommandException {
        if (obj == null) {
            throw new CommandFail(objectNull);
        }
        notNull.run();
        throw new CommandSuccess(success);
    }

    public static <T> void listNames(ExtronPlayer sender, String prefix, List<T> list, Function<T,String> convert, boolean showCount) {
        Collection<String> strings = ListUtils.convertAll(list,convert);
        StringBuilder b = new StringBuilder(prefix);
        int i = 0;
        for (String s : strings) {
            b.append(s);
            if (i == strings.size()-2) {
                b.append(" and ");
            } else if (i < strings.size()-2) {
                b.append(", ");
            }
            i++;
        }
        if (showCount) b.append(" (" + i + ")");
        sender.sendMessage(b.toString());
    }

    protected static void printHelpFor(ExtronPlayer sender, Command cmd) {
        printHelpFor(sender,cmd,0);
    }

    protected static void printHelpFor(ExtronPlayer sender, Command cmd, int indent) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            b.append("    ");
        }
        b.append(ChatColor.RED + "/" + ChatColor.YELLOW + String.join(" ",getNameHierarchy(cmd)));
        if (cmd.getArguments() != null && !cmd.getArguments().isEmpty()) {
            b.append(ChatColor.AQUA);
            for (Argument<?> arg : cmd.getArguments()) {
                b.append(" ");
                String end = arg.isRequired() ? ">" : "]";
                if (arg instanceof ArgStringList) {
                    end = "..." + end;
                } else if (arg instanceof ArgSubCommand) {
                    end = end + " [params...]";
                }
                if (arg.isRequired()) {
                    b.append("<" + arg.getName() + end);
                } else {
                    b.append("[" + arg.getName() + end);
                }
            }
        }
        b.append(ChatColor.RESET + ": ");
        b.append(ChatColor.GRAY + (cmd.getDescription() == null ? "" : cmd.getDescription()));
        if (cmd.getDescription() != null && (!cmd.getDescription().endsWith(".") && !cmd.getDescription().endsWith("!"))) b.append(".");
        sender.sendMessage(b.toString());
    }

    private static String[] getNameHierarchy(Command cmd) {
        List<String> names = new ArrayList<>();
        Command parent = Main.getCommandManager().getParent(cmd);
        if (parent != null) {
            getNameHierarchy(parent,names);
        }
        names.add(cmd.getName());
        return names.toArray(new String[0]);
    }

    private static void getNameHierarchy(Command cmd, List<String> names) {
        Command parent = Main.getCommandManager().getParent(cmd);
        if (parent != null) {
            getNameHierarchy(parent,names);
        }
        names.add(cmd.getName());
    }

    protected void performConditionedNull(Object obj, Runnable ifNotNull, Runnable ifNull) {
        if (obj == null && ifNull != null) {
            ifNull.run();
        } else if (obj != null && ifNotNull != null) {
            ifNotNull.run();
        }
    }

    protected boolean performConditioned(boolean cond, Runnable ifTrue, Runnable ifFalse) {
        if (cond && ifTrue != null) {
            ifTrue.run();
        } else if (!cond && ifFalse != null) {
            ifFalse.run();
        }
        return cond;
    }

    protected void performConditioned(boolean cond, String trueSucces, String falseError) throws CommandException {
        if (cond && trueSucces != null) {
            throw new CommandSuccess(trueSucces);
        } else if (!cond && falseError != null) {
            throw new CommandFail(falseError);
        }
    }

    protected boolean performConditioned(boolean cond, Runnable ifTrue, String falseError) throws CommandException {
        if (cond && ifTrue != null) {
            ifTrue.run();
        } else if (!cond && falseError != null) {
            throw new CommandFail(falseError);
        }
        return cond;
    }

    protected boolean performConditioned(boolean cond, String trueSuccess, Runnable ifFalse) throws CommandException {
        if (cond && trueSuccess != null) {
            throw new CommandSuccess(trueSuccess);
        } else if (!cond && ifFalse != null) {
            ifFalse.run();
        }
        return cond;
    }

    protected void success(String msg) throws CommandSuccess {
        throw new CommandSuccess(msg);
    }

    protected void success(String msg,Object... format) throws CommandSuccess {
        throw new CommandSuccess(String.format(msg,format));
    }

    protected static String translateChatColor(String text) {
    	char[] b = text.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    protected static void ifArgEquals(String arg, String equals, Runnable run) throws CommandSuccess {
        if (arg != null && arg.equalsIgnoreCase(equals)) {
            run.run();
            throw new CommandSuccess("");
        }
    }

    protected void test(boolean b, boolean testFor, String error) throws CommandFail {
        if (b != testFor) {
            throw new CommandFail(error);
        }
    }

    protected void error(String msg) throws CommandFail {
        throw new CommandFail(msg);
    }

    protected static void matchRegex(String text, String regex, String error) throws Exception {
        Pattern p = Pattern.compile("^\\w+$");
        if (!p.matcher(text).matches()) {
            throw new CommandFail(error);
        }
    }

    protected static void reExecute(ExtronPlayer p, Command cmd, ExecuteData data) {
        getCommandManager().forceExecute(p,cmd,data);
    }

    protected int parseInt(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    protected int parseInt(String str, String error) throws CommandException {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            throw new CommandFail(error);
        }
    }

    protected CommandFail tryParseInt(String str, Consumer<Integer> valueConsumer, String error) {
        try {
            int i =Integer.parseInt(str);
            valueConsumer.accept(i);
            return null;
        } catch (Exception e) {
            return new CommandFail(error);
        }
    }

    protected CommandFail tryParseLong(String str, Consumer<Long> valueConsumer, String error) {
        try {
            long l = Long.parseLong(str);
            valueConsumer.accept(l);
            return null;
        } catch (Exception e) {
            return new CommandFail(error);
        }
    }

    protected Exception getException(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    protected <T extends Enum<T>> T getEnumValue(Class<T> enumClass, String keyName, String value) throws CommandException {
        try {
            return Enum.valueOf(enumClass,value.toUpperCase());
        } catch (Exception e) {
            throw new CommandFail("invalid enum getValue for " + keyName + " \"" + value + "\"!");
        }
    }

    protected <T extends Enum<T>> Exception getEnumValueException(Class<T> enumClass, String keyName, String value, Consumer<T> noException) {
        try {
            T result = Enum.valueOf(enumClass,value.toUpperCase());
            if (noException != null) {
                noException.accept(result);
            }
        } catch (Exception e) {
            return new CommandFail("invalid enum getValue for " + keyName + " \"" + value + "\"!");
        }
        return null;
    }

    protected static <T> T nullOrGet(T obj, String error) throws CommandFail {
        if (obj == null) {
            throw new CommandFail(error);
        }
        return obj;
    }

    protected static <T> T notNullOrDefault(T obj, T def) {
        return obj == null ? def : obj;
    }
	
}
