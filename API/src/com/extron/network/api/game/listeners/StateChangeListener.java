package com.extron.network.api.game.listeners;

import com.extron.network.api.game.GameState;

public interface StateChangeListener extends GameListener {

    void onStateChanged(GameState from, GameState to);

}
