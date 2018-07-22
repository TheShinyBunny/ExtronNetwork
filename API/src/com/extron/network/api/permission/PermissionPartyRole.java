package com.extron.network.api.permission;

import com.extron.network.api.party.PartyMemberRole;
import com.extron.network.api.players.ExtronPlayer;

public class PermissionPartyRole {


    public Permission of(PartyMemberRole role) {
        return new PermissionPartyRole.Instance(role);
    }

    private static class Instance implements Permission {
        private final PartyMemberRole role;

        public Instance(PartyMemberRole role) {
            this.role = role;
        }


        @Override
        public String getCommandPermMessage(ExtronPlayer p) {
            return p.isInParty() ? role.getPermissionMessage() : "You must be " + role.getNiceName() + " in a party to use this command!";
        }

        @Override
        public boolean isPermitted(ExtronPlayer s) {
            return s.isInParty() && s.getParty().getRoleOf(s).getPos() >= role.getPos();
        }

    }
}
