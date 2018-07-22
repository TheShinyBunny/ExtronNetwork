package com.extron.network.api.game;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.config.Config;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class MapCreator {

    protected List<ExtronPlayer> creators;
    protected ExtronWorld map;
    private ExtronPlayer lastActive;

    public MapCreator(ExtronWorld map) {
        this.map = map;
        this.creators = new ArrayList<>();
    }

    public boolean placedBlock(Block b) {
        return false;
    }

    public boolean breakBlock(Block b) {
        return false;
    }

    public abstract void load(Config config);

    public abstract void save(Config config);

    public ExtronWorld getMap() {
        return map;
    }

    public abstract void getItems(Consumer<CreatorButton> c);

    public void message(String msg) {
        if (lastActive != null) {
            lastActive.sendMessage(msg);
        }
    }

    public void removeCreator(ExtronPlayer p) {
        creators.remove(p);
        if (creators.isEmpty()) {
            saveToConfig();
            map.setMapCreator(null);
        }
    }

    public void addCreator(ExtronPlayer p) {
        if (creators.isEmpty()) {
            loadFromConfig();
        }
        creators.add(p);
    }

    public void saveToConfig() {
        save(map.getConfig());
        map.getConfig().save();
    }

    public void loadFromConfig() {
        System.out.println("loading map creator from config");
        load(map.getConfig());
    }

    public void setActivePlayer(ExtronPlayer p) {
        this.lastActive = p;
    }
}
