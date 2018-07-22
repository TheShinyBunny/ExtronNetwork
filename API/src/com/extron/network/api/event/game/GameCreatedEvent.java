package com.extron.network.api.event.game;

import com.extron.network.api.game.GameMode;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.players.ExtronPlayer;

public class GameCreatedEvent extends GameEvent {
    private final ExtronPlayer creator;

    public GameCreatedEvent(ExtronPlayer creator, GameManager manager, GameMode game) {
        super(manager, game);
        this.creator = creator;
    }

    public ExtronPlayer getCreator() {
        return creator;
    }
}
