package com.extron.network.api.command;


import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class CommandTree extends BaseCommand {

    private List<Command> subCommands = new ArrayList<>();

    public abstract void addSubCommands(Consumer<Command> c);

    public abstract boolean addHelpSubCommand();

    public void add(Command subCommand) {
        this.subCommands.add(subCommand);
    }

    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {

    }

    @Override
    public Command[] getSubCommands() {
        return subCommands.toArray(new Command[0]);
    }

    public String getTreeName() {
        return "sub command";
    }

    public String getDefaultSubCommandToUse() {
        return null;
    }


    @Override
    public ExpectedArgs getArguments() {
        return new ExpectedArgs(this);
    }
}
