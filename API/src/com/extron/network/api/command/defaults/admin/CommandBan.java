package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.TimeStamp;
import com.extron.network.api.utils.punishment.BanReason;
import com.extron.network.api.utils.punishment.Punishment;

public class CommandBan extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ExtronPlayer p = getPlayerByName(args.getEntry("player"));
        BanReason reason = getEnumValue(args.getEntry("reason"),BanReason.NO_REASON);
        performConditioned(
                !PlayerList.punish(new Punishment(p,reason,new TimeStamp(),TimeStamp.never())),
                "Banned " + p.getName() + " for " + reason.getName() + "!",
                "This player is already banned!"
        );
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .player("player")
                .optional()
                .enumValue("reason",BanReason.class);
    }

    @Override
    public String getDescription() {
        return "Bans a player from the server. forever.";
    }

    @Override
    public Permission getPermission() {
        return Permission.STAFF;
    }
}
