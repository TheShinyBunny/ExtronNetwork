package com.extron.network.api.scoreboard;

import com.extron.network.api.Main;

public interface ScoreboardUpdater {

    boolean shouldUpdate(Scoreboard sb);

    default void updateScoreboard() {
        for (Scoreboard sb : Main.getScoreboards()) {
            if (this.shouldUpdate(sb)) {
                this.update(sb);
            }
        }
    }

    void update(Scoreboard sb);

}
