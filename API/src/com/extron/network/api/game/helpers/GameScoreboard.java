package com.extron.network.api.game.helpers;

import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.ScoreLines;
import com.extron.network.api.scoreboard.Scoreboard;

public abstract class GameScoreboard extends Scoreboard {

    protected final IGameManager manager;

    public GameScoreboard(IGameManager manager) {
        this.manager = manager;
    }

    @Override
    public void addLines(ExtronPlayer p, ScoreLines lines) {
        if (manager.getState().isBeforeStart()) {
            this.addPreGameLines(p,lines);
        } else {
            this.addIngameLines(p,lines);
        }
    }

    protected abstract void addPreGameLines(ExtronPlayer p, ScoreLines lines);

    protected abstract void addIngameLines(ExtronPlayer p, ScoreLines lines);
}
