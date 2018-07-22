package com.extron.network.api.game;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.inventory.Button;
import com.extron.network.api.inventory.ItemLore;
import com.extron.network.api.inventory.SimpleButton;
import com.extron.network.api.stats.Statistic;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public interface Game extends Listener {

    String getId();

    GameMode[] getGameModes();

    default List<ExtronWorld> getMaps() {
        List<ExtronWorld> maps = new ArrayList<>();
        for (GameMode g : getGameModes()) {
            maps.addAll(g.getMaps());
        }
        return maps;
    }

    default void registerStatistic(String id, String name) {
        Main.registerStatistic(new Statistic(id,name,this));
    }

    default List<Statistic> getStatistics() {
        return Main.getStatistics(this);
    }

    Material getIcon();

    default int getIconDamage() {
        return 0;
    }

    String getGameName();

    String getGameDescription();

    default Button createMenuButton() {
        return new SimpleButton(getIcon())
                .setDamage(getIconDamage())
                .setDisplayName(getGameName())
                .setLore(ItemLore.create().empty().description(getGameDescription()));
    }
}
