package com.extron.network.cards;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.*;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.Scoreboard;

public abstract class CardMinigame extends GameMode {
    @Override
    public int getTeamCount() {
        return 2;
    }

    @Override
    public int getPlayersInTeam() {
        return 1;
    }

    @Override
    public GameStartRules getStartRule() {
        return new GameStartRules().setMaxPlayers(2,10);
    }

    @Override
    public boolean hasTeamSelector(GameSettings gameSettings) {
        return false;
    }

    @Override
    public GameManager createManager(ExtronWorld map, GameSettings settings) {
        return new CardGameManager(this,map,settings);
    }

    @Override
    public boolean canPartyJoin() {
        return true;
    }

    @Override
    public DeathMessages getCustomDeathMessages() {
        return null;
    }

    @Override
    public void giveWaitItems(ExtronPlayer p, GameManager manager) {

    }

    @Override
    public Scoreboard createScoreboard(IGameManager manager) {
        return null;
    }

    @Override
    public String getShortName() {
        return getName();
    }
}
