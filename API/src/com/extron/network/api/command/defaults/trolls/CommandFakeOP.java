package com.extron.network.api.command.defaults.trolls;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

public class CommandFakeOP extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
        ExtronPlayer p = getOnlinePlayerByName(data.getEntry("player"));
        p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[%s: Opped %s]", sender.getName(),p.getName());
        success("Fake-OPed %s!",p.getName());
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
