package com.extron.network.api.game.managers;

import com.extron.network.api.game.Team;
import com.extron.network.api.players.ExtronPlayer;

import java.util.List;

public interface ICompetitiveManager extends IGameManager {

    Team getTeamOfPlayer(ExtronPlayer p);

    List<Team> getTeams();

    List<Team> getTeamsLeft();

    void teamEliminated(Team team);

    Team getTeam(String name);

    boolean areOnSameTeam(ExtronPlayer p1, ExtronPlayer p2);

    void setTeamOfPlayer(ExtronPlayer p, Team team);

    List<ExtronPlayer> getAlivePlayers();

    boolean isAlive(ExtronPlayer p);

    boolean isCompletelyAlive(ExtronPlayer p);

    List<Team> generateTeams(int teams);

    boolean isSpectator(ExtronPlayer player);
}
