package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;

public class CommandTitleAll extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        PlayerList.forEachOnline(p->p.sendTitle(args.at(0),""));
    }

    @Override
    public String getDescription() {
        return "Sends a title to everyone on the server";
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .stringList("text");
    }
}
