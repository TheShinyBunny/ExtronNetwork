package com.extron.network.api.permission;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.command.CommandSender;

public class PermissionRanked {

    public Permission min(Rank r) {
        return new Instance(r);
    }

    public static class Instance implements Permission{

        private Rank rank;

        public Instance(Rank r) {
            this.rank = r;
        }

        @Override
        public String getCommandPermMessage(ExtronPlayer p) {
            return "You must be " + rank.getName() + " or higher to use this command!";
        }

        @Override
        public boolean isPermitted(ExtronPlayer s) {
            return s.getRank().isAboveOrEqual(rank);
        }
    }

}
