package com.extron.network.api.command.defaults.trolls;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

public class CommandVanish extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        sender.setInvisible(!sender.isInvisible());
        success("You are now " + (sender.isInvisible() ? "INVISIBLE" : "VISIBLE"));
    }

    @Override
    public ExpectedArgs getArguments() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
