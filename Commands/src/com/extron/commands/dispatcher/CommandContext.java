package com.extron.commands.dispatcher;

import com.extron.commands.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class CommandContext {
    private final Player sender;
    private final CommandInfo info;
    private final String[] args;


    public CommandContext(Player sender, CommandInfo info, String[] rawArgs) {
        this.sender = sender;
        this.info = info;
        this.args = rawArgs;
    }

    public CommandSender getSender() {
        return sender;
    }

    public CommandInfo getInfo() {
        return info;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean hasArgs() {
        return getArgCount() > 0;
    }

    public int getArgCount() {
        return args.length;
    }


    public String argAt(int i) {
        return args[i];
    }

    public void invoke(Object[] array) throws InvocationTargetException, IllegalAccessException {
        this.info.run(array);
    }
}
