package com.extron.network.api.stats;

import com.extron.network.api.game.Game;

public class Statistic {

    private String id;
    private String name;
    private Game game;

    public Statistic(String id, String name, Game game) {
        this.id = id;
        this.name = name;
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }
}
