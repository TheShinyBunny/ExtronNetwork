package com.extron.network.api.command.exceptions;

import org.bukkit.ChatColor;

public class InvalidUsageException extends CommandException {


    public InvalidUsageException(String msg) {
        super(msg);
    }

    @Override
    public ChatColor getPrefix() {
        return ChatColor.RED;
    }
}
