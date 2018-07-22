package com.extron.network.api.game.listeners;

import com.extron.network.api.game.Team;
import com.extron.network.api.party.Party;
import com.extron.network.api.players.ExtronPlayer;

public interface TeamDivisionListener extends GameListener {


    void onPartyPlayerJoinedTeam(Party party, ExtronPlayer member, Team team);

    void onTeamFilledUp(Team t, ExtronPlayer member);

    void onPlayerJoinedTeam(Team t, ExtronPlayer p);
}
