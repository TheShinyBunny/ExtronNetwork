package com.extron.network.api.game.listeners;

import com.extron.network.api.game.Team;

public interface TeamsChangeListener extends GameListener {

    void onTeamCreated(Team t);

    boolean onTeamEliminated(Team t);


}
