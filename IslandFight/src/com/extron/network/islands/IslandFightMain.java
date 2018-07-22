package com.extron.network.islands;

import com.extron.network.api.GamePlugin;
import com.extron.network.api.game.GameMode;
import org.bukkit.Material;

public class IslandFightMain extends GamePlugin {

    private IslandFight gameMode;

    @Override
    protected void enable() {
        gameMode = new IslandFight();
    }

    @Override
    public String getId() {
        return "island_fight";
    }

    @Override
    public GameMode[] getGameModes() {
        return new GameMode[]{gameMode};
    }

    @Override
    public Material getIcon() {
        return Material.ENDER_PEARL;
    }

    @Override
    public String getGameName() {
        return "Island Fight";
    }

    @Override
    public String getGameDescription() {
        return "Team up with up to 5 players and open your own island. Collect resources and upgrade your buildings, and when you are ready, fight other islands!";
    }
}
