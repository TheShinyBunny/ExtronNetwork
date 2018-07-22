package com.extron.network.api.game.listeners;


public interface StartEndListener extends GameListener {

    void onGameStarted();

    void onGameEnded();

}
