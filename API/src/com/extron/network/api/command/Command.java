package com.extron.network.api.command;

import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;

import java.util.Arrays;
import java.util.Iterator;

public interface Command extends Iterable<Command>{
	
	String getName();
	
	void execute(ExtronPlayer sender, ExecuteData args) throws Exception;
	
	ExpectedArgs getArguments();
	
	String getDescription();
	
    @Override
    default Iterator<Command> iterator() {
        return Arrays.asList(getSubCommands()).iterator();
    }
    
    default String[] getAliases() {
        return new String[0];
    }

    default Command[] getSubCommands() {
        return new Command[0];
    }

	default Permission getPermission() {
		return Permission.ALL;
	}

	default boolean canConsoleUse() {
        return false;
    }
	
}
