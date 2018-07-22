package com.extron.network.api.game.managers;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.Death;
import com.extron.network.api.game.GameSettings;
import com.extron.network.api.game.Team;
import com.extron.network.api.game.listeners.TeamDivisionListener;
import com.extron.network.api.game.listeners.TeamsChangeListener;
import com.extron.network.api.party.Party;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class CompetitiveManager extends GameManager implements ICompetitiveManager {

    protected List<Team> aliveTeams;
    protected List<Team> allTeams;
    protected List<ExtronPlayer> alivePlayers;

    public CompetitiveManager(ExtronWorld map, GameSettings settings) {
        super(map, settings);
        allTeams = new ArrayList<>();
        aliveTeams = new ArrayList<>();
        alivePlayers = new ArrayList<>();
    }

    @Override
    public void start() {
        super.start();
        alivePlayers.addAll(allPlayers);
        List<Party> parties = new ArrayList<>();
        List<ExtronPlayer> notInParty = new ArrayList<>();
        Random r = new Random();
        for (ExtronPlayer p : allPlayers) {
            if (p.isInParty()) {
                if (!parties.contains(p.getParty())) {
                    parties.add(p.getParty());
                }
            } else {
                notInParty.add(p);
            }
        }
        int tc = 0;
        for (Party party : parties) {
            List<ExtronPlayer> players = new ArrayList<>(party.getAllPlayersInGame(this));
            while (!players.isEmpty()) {
                ExtronPlayer[] team = new ExtronPlayer[settings.getPlayersInTeam()];
                for (int x = 0; x < team.length; x++) {
                    if (players.size() > 1) {
                        team[x] = players.get(r.nextInt(players.size()));
                        players.remove(team[x]);
                    } else if (players.size() == 1) {
                        team[x] = players.get(0);
                        players.remove(team[x]);
                    } else {
                        break;
                    }
                }
                if (allTeams.get(tc).isFull()) {
                    tc++;
                }
                Team t = allTeams.get(tc);
                for (ExtronPlayer member : team) {
                    if (member != null) {
                        if (getTeamOfPlayer(member) == null) {
                            t.addMember(member);
                            invokeListener(TeamDivisionListener.class, d->d.onPartyPlayerJoinedTeam(party,member,t));
                        }
                        if (t.isFull()) {
                            invokeListener(TeamDivisionListener.class,d->d.onTeamFilledUp(t,member));
                            tc++;
                        }
                    }
                }
            }
        }
        List<Team> teamfiller = new ArrayList<>(allTeams);
        while (!teamfiller.isEmpty()){
            Team t;
            if (teamfiller.size() > 1) {
                t = teamfiller.get(r.nextInt(teamfiller.size()));
            } else {
                t = teamfiller.get(0);
            }
            if (notInParty.isEmpty()) {
                break;
            } else {
                ExtronPlayer p;
                if (notInParty.size() > 1) {
                    p = notInParty.get(r.nextInt(notInParty.size()));
                    if (getTeamOfPlayer(p) == null) {
                        t.addMember(p);
                    }
                    notInParty.remove(p);
                } else if (notInParty.size() == 1) {
                    p = notInParty.get(0);
                    if (getTeamOfPlayer(p) == null) {
                        t.addMember(p);
                        invokeListener(TeamDivisionListener.class,d->d.onPlayerJoinedTeam(t,p));
                    }
                    notInParty.remove(p);
                } else {
                    break;
                }
                if (t.isFull()) {
                    invokeListener(TeamDivisionListener.class,d->d.onTeamFilledUp(t,p));
                    teamfiller.remove(t);
                }
            }
        }
        for (ExtronPlayer p : alivePlayers) {
            p.getInventory().clear();
        }
        for (Team t : allTeams) {
            if (t.isAlive()) {
                aliveTeams.add(t);
            }
        }
        updateAllScoreboards(alivePlayers);
    }

    @Override
    public Team getTeamOfPlayer(ExtronPlayer p) {
        return ListUtils.firstMatch(allTeams,t->t.contains(p));
    }

    @Override
    public List<Team> getTeams() {
        return allTeams;
    }

    @Override
    public List<Team> getTeamsLeft() {
        return aliveTeams;
    }

    @Override
    public void teamEliminated(Team team) {
        System.out.println("a team has been eliminated!");
        if (!this.invokeResultListener(TeamsChangeListener.class,(t)->t.onTeamEliminated(team),false)) return;
        aliveTeams.remove(team);
        this.sendTeamEliminatedMessage(team);
        if (aliveTeams.size() < 2) {
            end();
        }
    }

    public Team getWinningTeam() {
        return aliveTeams.size() == 1 ? aliveTeams.get(0) : null;
    }

    @Override
    public Team getTeam(String name) {
        return ListUtils.firstMatch(allTeams,t->t.getName().equalsIgnoreCase(name));
    }

    @Override
    public void onPlayerJoined(ExtronPlayer p) {
        if (this.allTeams.isEmpty()) {
            allTeams = this.generateTeams(settings.getTeams());
        }
        super.onPlayerJoined(p);
    }

    @Override
    public boolean areOnSameTeam(ExtronPlayer p1, ExtronPlayer p2) {
        return this.getTeamOfPlayer(p1) != null && this.getTeamOfPlayer(p1).equals(this.getTeamOfPlayer(p2));
    }

    @Override
    public void setTeamOfPlayer(ExtronPlayer p, Team team) {
        getTeamOfPlayer(p).removeMember(p);
        team.addMember(p);
    }

    @Override
    public List<ExtronPlayer> getAlivePlayers() {
        return alivePlayers;
    }

    @Override
    public boolean isAlive(ExtronPlayer p) {
        return alivePlayers.contains(p);
    }

    @Override
    public boolean isCompletelyAlive(ExtronPlayer p) {
        return alivePlayers.contains(p) && !spectators.contains(p);
    }

    @Override
    public List<Team> generateTeams(int teams) {
        List<Team> list = new ArrayList<>();
        int c = 0;
        for (Map.Entry<ChatColor, String> e : DEFAULT_TEAMS.entrySet()) {
            list.add(new Team(e.getValue(), this, e.getKey()));
            c++;
            if (c == teams) break;
        }
        return list;
    }

    @Override
    public void onPlayerDeath(Death death) {
        super.onPlayerDeath(death);
        if (isFinalKill(death.getPlayer())) {
            alivePlayers.remove(death.getPlayer());
            getTeamOfPlayer(death.getPlayer()).memberFinalKilled(death.getPlayer());
        }
        updateAllScoreboards(allPlayers);
    }

    @Override
    public boolean isSpectator(ExtronPlayer player) {
        return spectators.contains(player);
    }
}
