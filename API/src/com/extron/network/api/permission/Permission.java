package com.extron.network.api.permission;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Permission {

    Permission PLAYERS = new PermissionBasic("Only players can use this command!",(s)-> s instanceof Player);
    Permission ALL = new PermissionBasic("You can use this command!");
    Permission OPERATORS = new PermissionBasic("operators",ExtronPlayer::isOp);
    Permission STAFF = new PermissionRanked().min(Rank.HELPER);
    Permission DEVS = new PermissionRanked().min(Rank.DEVELOPER);
    PermissionRanked RANKED = new PermissionRanked();
    PermissionPartyRole PARTY_ROLE = new PermissionPartyRole();

    String getCommandPermMessage(ExtronPlayer p);

    boolean isPermitted(ExtronPlayer s);

}
