package com.extron.network.api.command.exceptions;

import com.extron.network.api.command.argument.Argument;
import org.bukkit.ChatColor;

public class InvalidNumberException extends CommandException {

    private final Argument<?> arg;
    private final String val;

    public InvalidNumberException(Argument<?> a, String arg) {
        super("");
        this.arg = a;
        this.val = arg;
    }

    @Override
    public String getMessage() {
        return arg.getName() + " '" + val + "' is not a number!";
    }

    @Override
    public ChatColor getPrefix() {
        return ChatColor.RED;
    }
}
