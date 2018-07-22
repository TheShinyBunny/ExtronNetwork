package com.extron.network.islands;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.*;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.Scoreboard;

public class IslandFight extends GameMode {
    @Override
    public int getTeamCount() {
        return 1;
    }

    @Override
    public int getPlayersInTeam() {
        return 5;
    }

    @Override
    public GameStartRules getStartRule() {
        return new GameStartRules().addPlayerHook(3,0);
    }

    @Override
    public boolean hasTeamSelector(GameSettings gameSettings) {
        return false;
    }

    @Override
    public GameManager createManager(ExtronWorld map, GameSettings settings) {
        return new IslandManager(map,settings);
    }

    @Override
    public boolean canPartyJoin() {
        return false;
    }

    @Override
    public void giveWaitItems(ExtronPlayer p, GameManager manager) {

    }

    @Override
    public String getId() {
        return "island_fight";
    }

    @Override
    public Scoreboard createScoreboard(IGameManager manager) {
        return null;
    }

    @Override
    public String getName() {
        return "Island Fight";
    }

    @Override
    public DeathMessages getCustomDeathMessages() {
        return null;
    }

    @Override
    public MapCreator getNewMapCreator(ExtronWorld map) {
        return null;
    }

    @Override
    public String getShortName() {
        return "Island Fight";
    }

    @Override
    public boolean multiInstancesInOneMap() {
        return true;
    }
}
