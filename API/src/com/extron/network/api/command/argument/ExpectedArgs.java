package com.extron.network.api.command.argument;

import com.extron.network.api.Main;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.defaults.CommandHelp;
import com.extron.network.api.command.exceptions.InvalidArgumentException;
import com.extron.network.api.command.exceptions.InvalidUsageException;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ExpectedArgs implements Iterable<Argument<?>> {

    private List<Argument<?>> argList;
    private boolean hasOptionals;
    private boolean nextReplace = false;

    public ExpectedArgs() {
        this.argList = new ArrayList<>();
        this.hasOptionals = false;
    }

    public ExpectedArgs(CommandTree tree) {
        this();
        this.subCommand(tree);
    }

    public static ExpectedArgs create() {
        return new ExpectedArgs();
    }

    public ExpectedArgs string(String name, ValidArgs<String> valid) {
        return this.add(new ArgumentBase<>(name, valid, n->n));
    }

    public ExpectedArgs string(String name) {
        return this.string(name,ValidArgs.all());
    }

    public ExpectedArgs stringList(String name, ValidArgs<String> valid) {
        return this.add(new ArgStringList(name,valid,n->n));
    }

    public ExpectedArgs onlinePlayer(String name) {
        return string(name,ValidArgs.getter(ListUtils.toSupply(PlayerList.getOnlinePlayerNames())));
    }

    public ExpectedArgs player(String name) {
        return string(name,ValidArgs.getter(ListUtils.toSupply(PlayerList.getAllPlayerNames())));
    }

    public ExpectedArgs world(String name) {
        return string(name,ValidArgs.getter(Main::getWorldNames));
    }

    public ExpectedArgs stringList(String name) {
        return this.stringList(name,ValidArgs.all());
    }

    public ExpectedArgs number(String name, double min, double max) {
        return this.add(new ArgumentBase<>(name, ValidArgs.minMax(min,max),Double::parseDouble));
    }

    public ExpectedArgs number(String name) {
        return this.add(new ArgumentBase<>(name, ValidArgs.all(),Double::parseDouble));
    }

    /*public <E extends Enum<E> & Command> ExpectedArgs subCommand(String name, Class<E> enumClass) {
        return this.add(new ArgSubCommand(name,enumClass));
    }*/

    public ExpectedArgs subCommand(String name, Command... sc) {
        return this.add(new ArgSubCommand(name,sc));
    }

    public void subCommand(CommandTree tree) {
        if (tree.getSubCommands().length == 0) {
            tree.addSubCommands(tree::add);
            if (tree.addHelpSubCommand()) {
                tree.add(new CommandHelp(tree));
            }
        }
        this.add(new ArgSubCommand(tree.getTreeName(),tree.getSubCommands()));
    }

    public <E extends Enum<E>> ExpectedArgs enumValue(String name, Class<E> enumClass) {
        return this.add(new ArgumentBase<>(name,ValidArgs.fromEnum(enumClass,n->true), e->TextUtils.getEnumValue(enumClass,e)));
    }

    public <E extends Enum<E>> ExpectedArgs enumValue(String name, Class<E> enumClass, Predicate<E> filter) {
        return this.add(new ArgumentBase<>(name,ValidArgs.fromEnum(enumClass,filter),e->TextUtils.getEnumValue(enumClass,e)));
    }

    public ExpectedArgs bool(String name) {
        return this.add(new ArgumentBase<>(name, ValidArgs.getter(() -> Arrays.asList(true, false)),Boolean::valueOf));
    }



    private ExpectedArgs add(Argument<?> arg) {
        if (nextReplace && this.getLastArgument() != null) {
            this.getLastArgument().setReplacement(arg);
            nextReplace = false;
        }
        arg.setIndex(argList.size());
        if (hasOptionals) {
            arg.setRequired(false);
        }
        if (this.getLastArgument() != null) {
            if (this.getLastArgument() instanceof ArgSubCommand || this.getLastArgument() instanceof ArgStringList) {
                return this;
            }
        }
        this.argList.add(arg);
        return this;
    }

    public ExpectedArgs optional() {
        this.hasOptionals = true;
        return this;
    }

    public ExpectedArgs replacement() {
        this.nextReplace = true;
        return this;
    }

    public ExecuteData validate(ExtronPlayer sender, Command cmd, String alias, String[] args) throws Exception {
        if (this.getRequiredArgs().size() > args.length) {
            Command c = Main.getCommandManager().getParent(cmd);
            throw new InvalidUsageException("Invalid usage! use /help " + (c == null ? cmd.getName() : c.getName()));
        }
        ExecuteData data = new ExecuteData(cmd,alias);
        int i = 0;
        for (Argument<?> a : argList) {
            if (i >= args.length) {
                data.add(a,null);
            } else {
                if (cmd instanceof CommandTree && a instanceof ArgSubCommand) {
                    if (!a.isValid(sender,data,args[i]) && ((CommandTree) cmd).getDefaultSubCommandToUse() != null) {
                        Command sc = ListUtils.firstMatch(cmd.getSubCommands(), s -> s.getName().equalsIgnoreCase(((CommandTree) cmd).getDefaultSubCommandToUse()));
                        if (sc != null) {
                            ExpectedArgs ea = sc.getArguments();
                            if (ea == null) {
                                ea = ExpectedArgs.create();
                            }
                            return ea.validate(sender, sc, alias, args);
                        }
                        throw new InvalidArgumentException(a, args[i], "Unknown %s '%s'!");
                    }
                }
                if (!a.isValid(sender, data, args[i])) {
                    throw new InvalidArgumentException(a, args[i], "Unknown %s '%s'!");
                }
                if (a instanceof ArgStringList) {
                    data.add(a, String.join(" ", ListUtils.subArray(args, a.getIndex())));
                } else {
                    data.add(a, args[i]);
                }
            }
            i++;
        }
        return data;
    }

    private List<Argument<?>> getRequiredArgs() {
        return ListUtils.filter(argList,Argument::isRequired);
    }

    public List<String> tabComplete(ExtronPlayer sender, ExecuteData data, String arg, int index) {
        System.out.println("tab completes " + data.getCommand().getName() + " with '" + arg + "' at index " + index);
        List<String> list = new ArrayList<>();
        if (argList.size() == 0) {
            System.out.println("empty arg list");
            return list;
        }
        if (this.getLastArgument() instanceof ArgStringList && index >= this.getLastArgument().getIndex()) {
            index = this.getLastArgument().getIndex();
        }
        Argument<?> a = this.getArgument(index);
        if (a != null) {
            if (a.getValidArgs().getValid(sender,data).isEmpty()) {
                System.out.println("empty valid");
                if (a.getReplacement() != null){
                    if (a.getReplacement().getValidArgs().getValid(sender, data).isEmpty()) {
                        return list;
                    }
                } else {
                    return list;
                }
            }
            for (String s : ListUtils.toStringAll(a.getValidArgs().getValid(sender,data))) {
                if (TextUtils.startsWithIgnoreCase(s,arg) || arg.isEmpty()) {
                    System.out.println("added valid completion '" + s + "'");
                    list.add(a.getValidArgs().isLower() ? s.toLowerCase() : s);
                }
            }
            if (a.getReplacement() != null) {
                for (String s : ListUtils.toStringAll(a.getReplacement().getValidArgs().getValid(sender, data))) {
                    if (TextUtils.startsWithIgnoreCase(s, arg) || arg.isEmpty()) {
                        list.add(a.getReplacement().getValidArgs().isLower() ? s.toLowerCase() : s);
                    }
                }
            }
        } else {
            System.out.println("arg not found!");
        }
        return list;
    }

    private Argument<?> getArgument(int index) {
        return ListUtils.firstMatch(argList,a->a.getIndex()==index);
    }

    private Argument<?> getLastArgument() {
        return argList.isEmpty() ? null : argList.get(argList.size()-1);
    }

    public List<Argument<?>> toList() {
        return argList;
    }

    public boolean isEmpty() {
        return this.argList.isEmpty();
    }

    @Override
    public Iterator<Argument<?>> iterator() {
        return this.argList.iterator();
    }

    public ExpectedArgs params() {
        return stringList("params");
    }


}
