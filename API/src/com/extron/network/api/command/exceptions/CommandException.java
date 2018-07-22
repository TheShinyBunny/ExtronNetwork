package com.extron.network.api.command.exceptions;

import org.bukkit.ChatColor;

public abstract class CommandException extends Exception {

    public CommandException(String msg) {
        super(msg);
    }


    public abstract ChatColor getPrefix();
}
