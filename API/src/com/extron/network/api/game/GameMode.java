package com.extron.network.api.game;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public abstract class GameMode {
    private List<GameManager> managers;
    private List<ExtronWorld> maps;

    public GameMode() {
        this.managers = new ArrayList<>();
        this.maps = new ArrayList<>();
    }

    public abstract int getTeamCount();

    public abstract int getPlayersInTeam();

    public abstract GameStartRules getStartRule();

    public abstract boolean hasTeamSelector(GameSettings gameSettings);

    public abstract GameManager createManager(ExtronWorld map, GameSettings settings);

    public abstract boolean canPartyJoin();

    public void addManager(GameManager manager) {
        this.managers.add(manager);
    }

    public List<GameManager> getManagers() {
        return managers;
    }

    public abstract void giveWaitItems(ExtronPlayer p, GameManager manager);

    public List<ExtronWorld> getMaps() {
        return maps;
    }

    public abstract String getId();

    public int getMaxPlayers() {
        return getTeamCount() * getPlayersInTeam();
    }

    public abstract Scoreboard createScoreboard(IGameManager manager);

    public abstract String getName();

    public abstract DeathMessages getCustomDeathMessages();

    public abstract MapCreator getNewMapCreator(ExtronWorld map);

    public abstract String getShortName();

    public abstract boolean multiInstancesInOneMap();
}
