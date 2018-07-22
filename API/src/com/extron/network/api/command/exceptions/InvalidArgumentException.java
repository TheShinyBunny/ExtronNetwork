package com.extron.network.api.command.exceptions;

import com.extron.network.api.command.argument.Argument;
import org.bukkit.ChatColor;

public class InvalidArgumentException extends CommandException {

	private final Argument<?> arg;
    private final String val;

    public InvalidArgumentException(Argument<?> a, String arg, String format) {
        super(format);
        this.arg = a;
        this.val = arg;
    }

    @Override
    public String getMessage() {
        return String.format(super.getMessage(),arg.getName(),val);
    }

    @Override
    public ChatColor getPrefix() {
        return ChatColor.RED;
    }
}
