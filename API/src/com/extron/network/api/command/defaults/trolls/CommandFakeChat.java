package com.extron.network.api.command.defaults.trolls;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

public class CommandFakeChat extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
        ExtronPlayer p = getOnlinePlayerByName(data.getEntry("player"));
        String msg = data.at(1);
        p.getWorld().broadcastMessage(p,msg);
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .onlinePlayer("player")
                .stringList("message");
    }

    @Override
    public String getDescription() {
        return null;
    }
}
