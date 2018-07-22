package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

import java.util.function.Consumer;

public class CommandGame extends CommandTree {
    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "forcestart";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                isNull(sender.getCurrentGame(),"You are not in a game!");
                sender.getCurrentGame().forceStart(sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
    }

    @Override
    public boolean addHelpSubCommand() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
