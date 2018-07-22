package com.extron.network.api.permission;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.command.CommandSender;

import java.util.function.Predicate;

public class PermissionBasic implements Permission {

    private final String commandPerm;
    private final Predicate<ExtronPlayer> predicate;

    public PermissionBasic(String commandPerm, Predicate<ExtronPlayer> permitted) {
        this.commandPerm = commandPerm;
        this.predicate = permitted;
    }

    public PermissionBasic(Predicate<ExtronPlayer> permitted) {
        this("You have no permissions to use this command!",permitted);
    }

    public PermissionBasic(String commandPerm) {
        this(commandPerm,s->true);
    }

    @Override
    public String getCommandPermMessage(ExtronPlayer p) {
        return commandPerm;
    }

    @Override
    public boolean isPermitted(ExtronPlayer s) {
        return predicate == null || predicate.test(s);
    }

}
