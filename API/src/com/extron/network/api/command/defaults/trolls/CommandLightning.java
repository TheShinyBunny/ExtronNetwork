package com.extron.network.api.command.defaults.trolls;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

public class CommandLightning extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ExtronPlayer p = getOnlinePlayerByName(args.getEntry("player"));
        p.strikeLightning();
        success("Striked " + p.getName());
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .onlinePlayer("player");
    }

    @Override
    public String getDescription() {
        return null;
    }
}
