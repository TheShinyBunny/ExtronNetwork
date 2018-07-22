package com.extron.network.api.command.argument;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.exceptions.CommandException;
import com.extron.network.api.command.exceptions.CommandFail;
import com.extron.network.api.command.exceptions.InvalidArgumentException;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExecuteData {

    private String alias;
    private Map<Argument<?>,String> args;
    private ExecuteData parent;
    private Command cmd;

    public ExecuteData(Command c, String alias) {
        this.cmd = c;
        this.alias = alias;
        this.args = new HashMap<>();
    }

    public void add(Argument<?> arg, String value) {
        this.args.put(arg,value);
    }

    public Map<Argument<?>, String> getAll() {
        return args;
    }

    public String get(String key) {
        Entry<Argument<?>,String> arg = ListUtils.firstMatch(args.entrySet(), e->e.getKey().getName().equalsIgnoreCase(key));
        return arg == null ? null : arg.getValue();
    }

    public int getInt(String key) throws InvalidArgumentException {
        String val = get(key);
        if (val == null) return -1;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(getArgument(key),val,"Invalid %s number '%s'!");
        }
    }

    public int getInt(String key, int def) throws InvalidArgumentException {
        return getInt(key) == -1 ? def : getInt(key);
    }

    public Argument<?> getArgument(String key) {
        return ListUtils.firstMatch(args.keySet(),e->e.getName().equalsIgnoreCase(key));
    }

    public Pair<Argument<?>,String> getEntry(String key) {
        return Pair.of(getArgument(key),get(key));
    }

    public Pair<ArgSubCommand,Command> getSubCommand() {
        Entry<Argument<?>,String> sc = ListUtils.firstMatch(args.entrySet(), e->e.getKey() instanceof ArgSubCommand);
        return sc == null ? null : Pair.of((ArgSubCommand)sc.getKey(),((ArgSubCommand)sc.getKey()).getSubCommand(sc.getValue()));
    }

    public int getSubCommandIndex() {
        return getSubCommand() == null ? -1 : indexOf(getSubCommand().getFirst().getName());
    }

    public int indexOf(String key) {
        return ListUtils.firstIndex(args.keySet(),e->e.getName().equalsIgnoreCase(key));
    }

    public ExecuteData merge(ExecuteData other) {
        other.parent = this;
        return other;
    }

    public String at(int index) {
        Entry<Argument<?>,String> a = ListUtils.firstMatch(args.entrySet(), e->e.getKey().getIndex()==index);
        return a == null ? null : a.getValue();
    }

    public ExecuteData getParent() {
        return parent;
    }

    public String getAlias() {
        return alias;
    }

    public Command getCommand() {
        return cmd;
    }

    public int getIntOfRange(String name, int def, int min, int max) throws CommandException {
        int i = this.getInt(name,def);
        if ((i > max && max != -1) || i < min) {
            throw new CommandFail(name + " must be between " + min + " and " + max);
        }
        return i;
    }

    public int length() {
        int i = 0;
        for (Entry<Argument<?>,String> e : args.entrySet()) {
            if (e != null && e.getValue() != null) {
                i++;
            }
        }
        return i;
    }

    public List<String> getList(String key) {
        String s = this.get(key);
        if (s == null) return null;
        String[] arr = s.split(" ");
        return Arrays.asList(arr);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Entry<Argument<?>,String> e : args.entrySet()) {
            b.append(e.getKey().getName() + " = " + (e.getValue() == null ? "null" : e.getValue()) + ", ");
        }
        return b.toString();
    }

    public boolean isEmpty() {
        return length() == 0;
    }
}
