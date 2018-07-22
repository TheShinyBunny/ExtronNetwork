package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.ListUtils;

public class CommandUnban extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ExtronPlayer p = getPlayerByName(args.getEntry("player"));
        performConditioned(
                PlayerList.unban(p),
                "Unbanned player " + p.getName() + "!",
                "This player is not banned!"
        );
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .string("player",ValidArgs.getter(ListUtils.convertAndSupply(PlayerList.getBannedPlayers(),ExtronPlayer::getName)));
    }

    @Override
    public String getDescription() {
        return "Unbans a banned player from the server.";
    }

    @Override
    public Permission getPermission() {
        return Permission.STAFF;
    }
}
