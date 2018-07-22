package com.extron.network.api.command.exceptions;
import org.bukkit.ChatColor;

public class CommandFail extends CommandException {
    public CommandFail(String msg) {
        super(msg);
    }

    @Override
    public ChatColor getPrefix() {
        return ChatColor.RED;
    }
}
