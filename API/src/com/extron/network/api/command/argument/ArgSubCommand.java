package com.extron.network.api.command.argument;

import com.extron.network.api.command.Command;
import com.extron.network.api.utils.ListUtils;

import java.util.Arrays;

public class ArgSubCommand extends ArgumentBase<String> {
    private Command[] subCommands;

    public ArgSubCommand(String name, Command... subCommands) {
        super(name, ValidArgs.getter(ListUtils.convertAndSupply(Arrays.asList(subCommands),Command::getName)), s->s);
        this.subCommands = subCommands;
    }
/*

    public <E extends Enum<E> & Command> ArgSubCommand(String name, Class<E> enumClass) {
        super(name,ValidArgs.all());
        for (E sc : enumClass.getEnumConstants()) {

        }
    }
*/

    public Command getSubCommand(String alias) {
        for (Command sc : subCommands) {
            if (sc.getName().equalsIgnoreCase(alias)) {
                return sc;
            }
            for (String a : sc.getAliases()) {
                if (a.equalsIgnoreCase(alias)) {
                    return sc;
                }
            }
        }
        return null;
    }
}
