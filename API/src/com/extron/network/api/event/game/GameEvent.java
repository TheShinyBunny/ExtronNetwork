package com.extron.network.api.event.game;

import com.extron.network.api.event.ExtronEvent;
import com.extron.network.api.game.GameMode;
import com.extron.network.api.game.managers.GameManager;

public class GameEvent extends ExtronEvent {

    private GameManager manager;
    private GameMode game;

    public GameEvent(GameManager manager, GameMode game) {
        this.manager = manager;
        this.game = game;
    }

    public GameMode getGame() {
        return game;
    }

    public GameManager getManager() {
        return manager;
    }
}
