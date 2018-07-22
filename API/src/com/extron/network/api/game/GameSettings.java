package com.extron.network.api.game;

import com.extron.network.api.players.ExtronPlayer;

public class GameSettings {

    private final GameMode gameMode;
    private final ExtronPlayer owner;
    private int teams;
    private int playersInTeam;
    private GameStartRules gameStartRules;
    private boolean teamSelector;

    public GameSettings(GameMode gameMode, ExtronPlayer p) {
        this.owner = p;
        this.gameMode = gameMode;
        this.teams = gameMode.getTeamCount();
        this.playersInTeam = gameMode.getPlayersInTeam();
        this.gameStartRules = gameMode.getStartRule();
        this.teamSelector = gameMode.hasTeamSelector(this);
    }

    public int getPlayersInTeam() {
        return playersInTeam;
    }

    public int getTeams() {
        return teams;
    }

    public void setTeams(int i) {
        this.teams = i;
    }

    public boolean hasTeamSelector() {
        return teamSelector;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setTeamSelector(boolean teamSelector) {
        this.teamSelector = teamSelector;
    }

    public void setPlayersInTeam(int i) {
        this.playersInTeam = i;
    }

    public GameStartRules getGameStartRules() {
        return gameStartRules;
    }

    public ExtronPlayer getOwner() {
        return owner;
    }
}
