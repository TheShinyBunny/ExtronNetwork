package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;

public class CommandReply extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        isNull(sender.privateMassaging,"There are no recent messages!");
        performConditioned(sender.privateMassaging.isOnline(),(String)null,"The last player sent a private message to you is not online!");
        sender.sendPrivateMessage(args.at(0));
    }

    @Override
    public String[] getAliases() {
        return new String[]{"r"};
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .stringList("message");
    }

    @Override
    public String getDescription() {
        return "Reply to the most recent private message sent to you.";
    }
}
