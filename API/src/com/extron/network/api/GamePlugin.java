package com.extron.network.api;

import com.extron.network.api.event.network.ExtronLoadedEvent;
import com.extron.network.api.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GamePlugin extends JavaPlugin implements Game,Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    @EventHandler
    public void loaded(ExtronLoadedEvent e) {
        this.enable();
        Main.addGame(this);
    }

    protected abstract void enable();
}
