package com.extron.network.skywars;

import com.extron.network.api.game.helpers.GameScoreboard;
import com.extron.network.api.game.managers.ICompetitiveManager;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.game.managers.IPvPManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.ScoreLines;
import com.extron.network.api.utils.AnimatedText;
import org.bukkit.ChatColor;

public class SkyWarsBoard extends GameScoreboard {
    public SkyWarsBoard(IGameManager manager) {
        super(manager);
    }

    @Override
    protected void addPreGameLines(ExtronPlayer p, ScoreLines lines) {
        if (((SkyWarsManager)manager).isInCages()) {
            addIngameLines(p,lines);
            return;
        }
        lines.addEmpty();
        if (manager.getStartCountdown() >= 0) {
            lines.add("Status: " + ChatColor.GREEN + "Starting in " + manager.getStartCountdown() + "s");
        } else {
            lines.add("Status: " + ChatColor.GRAY + "Waiting...");
        }
        lines.addEmpty();
        lines.addParam("Players",manager.getWaiting().size());
        lines.addEmpty();
        lines.addParam("Mode",ChatColor.BLUE + manager.getGameMode().getShortName());
        lines.addParam("Map",manager.getMap().getName());
        lines.addEmpty();
        lines.addWebsite();
    }

    @Override
    protected void addIngameLines(ExtronPlayer p, ScoreLines lines) {
        lines.add(ChatColor.GRAY + manager.getGameMode().getShortName());
        lines.addEmpty();
        lines.addParam("Refill In","Unknown");
        lines.addEmpty();
        lines.addParam("Players Left",((ICompetitiveManager)manager).getAlivePlayers().size());
        lines.addParam("Kills",((IPvPManager)manager).getKillsOf(p));
        lines.addEmpty();
        lines.addWebsite();
    }

    @Override
    public String getId() {
        return "skywars";
    }

    @Override
    public String getTitle() {
        return ChatColor.GOLD + "SkyWars";
    }

    @Override
    public AnimatedText getTitleAnimation() {
        return null;
    }
}
