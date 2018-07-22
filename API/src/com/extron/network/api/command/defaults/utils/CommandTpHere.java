package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

public class CommandTpHere extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ExtronPlayer p = getOnlinePlayerByName(args.getEntry("player"));
        sender.executeCommand("tp " + p.getName() + " " + sender.getName());
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .onlinePlayer("player");
    }

    @Override
    public String getDescription() {
        return "Teleports a player to your location";
    }
}
