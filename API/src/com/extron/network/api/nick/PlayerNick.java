package com.extron.network.api.nick;

import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerData;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.scoreboard.ScoreboardManager;
import com.extron.network.api.utils.Savable;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class PlayerNick implements Savable<PlayerData> {

    private ExtronPlayer player;
    private String realName;
    private String nickName;
    private Rank realRank;
    private Rank nickRank;
    private boolean isNicked;
    private ScoreboardManager scoreboard;
    private Map<Rank,Team> teams;

    public PlayerNick(ExtronPlayer player) {
        this.player = player;
        this.realName = player.getName();
        this.realRank = player.getRank();
        this.isNicked = false;
        this.scoreboard = player.getScoreboardManager();
    }

    public void init() {
        this.setupTeams();
        this.setDisplay();
        if (isNicked) {
            NickHelper.nickPlayer(player, getNickName(), getNickRank(),false);
        }
        this.updateRankDisplay();
        this.updateAll();
    }

    private void updateRankDisplay() {
        this.updateRankOf(this.player);
    }

    private void setupTeams() {
        if (teams == null) {
            this.teams = new HashMap<>();
        }
        for (Rank r : Rank.ALL) {
            try {
                this.teams.put(r,this.scoreboard.getHandle().registerNewTeam(r.getId()));
            } catch (Exception e){
                this.teams.put(r, this.scoreboard.getHandle().getTeam(r.getId()));
            }
        }
    }

    private void setDisplay() {
        for (Rank r : Rank.ALL) {
            Team t = this.teams.get(r);
            t.setPrefix(r.getPrefix() + r.getNameColor());
        }
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public Rank getNickRank() {
        return isNicked ? nickRank : realRank;
    }

    public Rank getRealRank() {
        return realRank;
    }

    public String getNickName() {
        return isNicked ? nickName : realName;
    }

    public String getRealName() {
        return realName;
    }

    public String getRealDisplayName() {
        return realRank.getPrefix() + ChatColor.RESET + realName;
    }

    public boolean isNicked() {
        return isNicked;
    }

    public String getDisplayName() {
        return isNicked ? nickRank.getPrefix() + ChatColor.RESET + nickName : getRealDisplayName();
    }

    @Override
    public void load(PlayerData obj) {
        this.nickName = obj.getString("nick.name");
        this.nickRank = Rank.fromString(obj.getString("nick.rank"));
        this.isNicked = nickName != null && nickRank != null;
    }

    public void nick(String name, Rank rank) {
        this.nickName = name;
        this.nickRank = rank;
        this.isNicked = true;
        this.save();
    }

    @Override
    public void save() {
        player.setData("nick.name",nickName);
        player.setData("nick.rank",nickRank == null ? null : nickRank.getId());
    }

    public void unnick() {
        this.nickName = null;
        this.nickRank = null;
        this.isNicked = false;
        player.setData("nick",null);
    }

    public static void update(ExtronPlayer p) {
        for (ExtronPlayer player : PlayerList.getOnlinePlayers()) {
            player.getNickManager().updateRankOf(p);
        }
    }

    public void updateRankOf(ExtronPlayer p) {
        this.clearTeams(p);
        this.teams.get(p.getNickRank()).addPlayer(p.handle);
    }

    private void clearTeams(ExtronPlayer p) {
        for (Team t : scoreboard.getHandle().getTeams()) {
            t.removePlayer(p.handle);
        }
    }

    public void updateAll() {
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            this.updateRankOf(p);
        }
    }

    public void setNickRank(Rank r) {
        this.nickRank = r;
        update(player);
    }

    public void setRealRank(Rank rank) {
        this.realRank = rank;
    }
}
