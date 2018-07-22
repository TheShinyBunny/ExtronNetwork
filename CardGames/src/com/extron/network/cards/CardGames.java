package com.extron.network.cards;

import com.extron.network.api.GamePlugin;
import com.extron.network.api.game.GameMode;
import org.bukkit.Material;

public class CardGames extends GamePlugin {

    private Taki taki;

    @Override
    protected void enable() {
        taki = new Taki();
    }

    @Override
    public String getId() {
        return "card_games";
    }

    @Override
    public GameMode[] getGameModes() {
        return new GameMode[]{taki};
    }

    @Override
    public Material getIcon() {
        return null;
    }

    @Override
    public String getGameName() {
        return null;
    }

    @Override
    public String getGameDescription() {
        return null;
    }
}
