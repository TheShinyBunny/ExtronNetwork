package com.extron.network.api.command.exceptions;

import org.bukkit.ChatColor;

public class CommandSuccess extends CommandException {
    public CommandSuccess(String success) {
        super(success);
    }

    @Override
    public ChatColor getPrefix() {
        return ChatColor.GREEN;
    }
}
