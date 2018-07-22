package com.extron.commands.dispatcher;

import com.extron.commands.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.List;

public class Dispatcher {

    private static List<CommandInfo> commands = new ArrayList<>();
    private static List<AnnotationAdapter<? extends Annotation>> annotationAdapters = new ArrayList<>();
    private static List<ArgumentAdapter> argumentAdapters = new ArrayList<>();
    private static List<AdditionalParser<?,?>> additionalParsers = new ArrayList<>();

    public static Object parseArgument(String input, Class<?> type, String argName) throws ExecuteException {
        if (type.isEnum()) {
            for (Object o : type.getEnumConstants()) {
                if (o.toString().equalsIgnoreCase(input)) {
                    return o;
                }
            }
        }
        for (ArgumentAdapter<?> a : argumentAdapters) {
            if (a.getType().isAssignableFrom(type)) {
                try {
                    return a.parse(input,argName);
                } catch (ExecuteException ignored) {

                }
            }
        }
        for (AdditionalParser<?,?> a : additionalParsers) {
            if (a.getResultType().isAssignableFrom(type)) {
                return a.apply(input);
            }
        }
        return null;
    }

    public static void handle(Player sender, String msg) {
        msg = msg.trim();
        if (msg.charAt(0) == '/') {
            msg = msg.substring(1);
        }
        String[] rawArgs = msg.split("\\.");
        String name = rawArgs[0];
        rawArgs = Arrays.copyOfRange(rawArgs,1,rawArgs.length);
        CommandInfo i = getCommand(name);
        if (i == null) {
            return;
        }
        dispatch(new CommandContext(sender,i,rawArgs));
    }

    public static void dispatch(CommandContext context) {
        try {
            List<Object> args = new ArrayList<>();
            if (context.hasArgs()) {
                int i = 0;
                boolean parse = true;
                for (Parameter p : context.getInfo().getAllParameters()) {
                    if (parse) {
                        String current = context.argAt(i);
                        Object obj = parseArgument(current, p.getType(), p.getName());
                        for (AnnotationAdapter a : annotationAdapters) {
                            if (p.isAnnotationPresent(a.getAnnotationType())) {
                                if (a.isValidParamType(p.getType())) {
                                    if (obj == null && !a.isArgument()) {
                                        obj = a.outOfSyntax(context);
                                    }
                                    obj = a.validate(obj, p.getAnnotation(a.getAnnotationType()), p.getType(), p.getName());
                                }
                            }
                        }
                        args.add(obj);
                    } else if (args.size() == context.getInfo().getAllParameters().length) {
                        break;
                    } else if (isRequired(p)) {
                        throw InvalidArgumentException.invalidUsage();
                    }
                    i++;
                    if (i >= context.getArgCount()) {
                        parse = false;
                    }
                }
            }
            context.invoke(args.toArray());
        } catch (Exception e) {
            context.getSender().sendMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isRequired(Parameter p) {
        for (AnnotationAdapter a : annotationAdapters) {
            if (p.isAnnotationPresent(a.getAnnotationType())) {
                if (a.isRequired()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> tabComplete(CommandSender sender, String msg) {
        msg = msg.trim();
        if (msg.charAt(0) == '/') {
            msg = msg.substring(1);
        }
        if (msg.contains(" ")) {
            String name = msg.substring(0,msg.indexOf(" "));
            CommandInfo i = getCommand(name);
            if (i == null) {
                return new ArrayList<>();
            }
            String arg = msg.substring(msg.lastIndexOf(" ")+1);
            int argIndex = StringUtils.countMatches(msg.substring(1)," ");
            List<String> list = getTabCompletions(sender,i,argIndex);
            if (!arg.isEmpty()) {
                list.removeIf(s -> !s.toLowerCase().startsWith(arg.toLowerCase()));
            }
            return list;
        } else {
            List<String> cmds = new ArrayList<>();
            for (CommandInfo i : commands) {
                for (String a : i.getAliases()) {
                    if (a.toLowerCase().startsWith(msg.toLowerCase()) || msg.isEmpty()) {
                        cmds.add(a);
                    }
                }
            }
            return cmds;
        }
    }

    public static List<String> getTabCompletions(CommandSender sender, CommandInfo info, int index) {
        int i = 0;
        List<String> list = new ArrayList<>();
        for (Parameter p : getArguments(info.getMethod())) {
            if (i == index) {
                for (AnnotationAdapter<?> a : annotationAdapters) {
                    if (p.isAnnotationPresent(a.getAnnotationType())) {
                        Collection<String> c = a.getAnnotatedCompletions(p);
                        if (c != null && !c.isEmpty()) {
                            list.addAll(c);
                        }
                        Collection<String> c2 = a.getSenderCompletions(sender);
                        if (c2 != null && !c2.isEmpty()) {
                            list.addAll(c2);
                        }
                    }
                }
                for (ArgumentAdapter<?> a : argumentAdapters) {
                    if (a.getType().isAssignableFrom(p.getType())) {
                        Collection<String> c = a.getArgCompletions(p);
                        if (c != null && !c.isEmpty()) {
                            list.addAll(c);
                        }
                    }
                }
            }
            i++;
        }
        return list;
    }

    public static void register(CommandHolder holder) {
        for (Method m : holder.getClass().getDeclaredMethods()) {
            register(holder,m);
        }
    }

    public static void register(CommandHolder holder, Method m) {
        if (m.isAnnotationPresent(Command.class)) {
            register(holder, m.getAnnotation(Command.class), m);
        }
    }

    public static void register(CommandHolder holder, Command annotation, Method m) {
        register(new CommandInfo(holder,m,m.getName(),annotation));
    }

    public static void register(CommandInfo info) {
        commands.add(info);
    }

    public static CommandInfo getCommand(String name) {
        for (CommandInfo i : commands) {
            for (String s : i.getAliases()) {
                if (s.equalsIgnoreCase(name)) {
                    return i;
                }
            }
        }
        return null;
    }

    public static List<Parameter> getArguments(Method method) {
        List<Parameter> list = new ArrayList<>();
        for (Parameter p : method.getParameters()) {
            boolean found = false;
            for (AnnotationAdapter<?> a : annotationAdapters) {
                if (p.isAnnotationPresent(a.getAnnotationType())) {
                    found = true;
                    if (a.isArgument()) {
                        list.add(p);
                        break;
                    }
                }
            }
            if (!found) {
                list.add(p);
            }
        }
        return list;
    }

    public static <T,R> void registerAdditionalParser(AdditionalParser<T,R> additionalParser) {
        additionalParsers.add(additionalParser);
    }
}
