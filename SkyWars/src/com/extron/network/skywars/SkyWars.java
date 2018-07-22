package com.extron.network.skywars;

import com.extron.network.api.GamePlugin;
import com.extron.network.api.Main;
import com.extron.network.api.event.network.ExtronLoadedEvent;
import com.extron.network.api.game.Game;
import com.extron.network.api.game.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyWars extends GamePlugin {

    private SkyWarsSolo solo;

    @Override
    protected void enable() {
        solo = new SkyWarsSolo();
    }

    @Override
    public String getId() {
        return "skywars";
    }

    @Override
    public GameMode[] getGameModes() {
        return new GameMode[] {solo};
    }

    @Override
    public Material getIcon() {
        return Material.GRASS;
    }

    @Override
    public String getGameName() {
        return "SkyWars";
    }

    @Override
    public String getGameDescription() {
        return "Sky wars, but better!";
    }

}
