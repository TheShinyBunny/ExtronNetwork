package com.extron.network.cards;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.MapCreator;

public class Taki extends CardMinigame {
    @Override
    public String getId() {
        return "taki";
    }

    @Override
    public String getName() {
        return "Taki";
    }

    @Override
    public MapCreator getNewMapCreator(ExtronWorld map) {
        return null;
    }

    @Override
    public boolean multiInstancesInOneMap() {
        return true;
    }
}
