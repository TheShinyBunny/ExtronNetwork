package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ChatType;
import org.bukkit.ChatColor;

public class CommandMessage extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
        ExtronPlayer p = getOnlinePlayerByName(data.getEntry("player"));
        test(sender == p,false,"You can't message yourself!");
        p.privateMassaging = sender;
        p.privateMsgDelay = 3600;
        sender.privateMassaging = p;
        sender.privateMsgDelay = 3600;
        if (data.at(1) == null) {
            sender.chat = ChatType.PRIVATE;
            success("You have started a private conversation with " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + ". Use '/chat a' to go back to the Public Chat.");
        } else {
            p.sendPrivateMessage(data.at(1));
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"msg","w","tell"};
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .onlinePlayer("player")
                .optional()
                .stringList("message");
    }

    @Override
    public String getDescription() {
        return "Sends a private message to a player";
    }
}
