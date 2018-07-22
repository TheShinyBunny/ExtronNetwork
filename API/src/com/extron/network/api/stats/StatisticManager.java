package com.extron.network.api.stats;

import com.extron.network.api.Main;
import com.extron.network.api.game.Game;
import com.extron.network.api.inventory.ItemLore;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.Loadable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticManager implements Loadable<List<Statistic>> {

    private ExtronPlayer player;
    private Map<Statistic,Integer> map;

    public StatisticManager(ExtronPlayer p) {
        this.player = p;
        this.map = new HashMap<>();
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public int get(Statistic s) {
        return map.getOrDefault(s, 0);
    }

    public void increment(Statistic s) {
        this.increment(s,1);
    }

    public void increment(Statistic s, int amount) {
        if (get(s) == 0) {
            this.map.put(s, 0);
        }
        this.set(s, this.get(s) + amount);
    }

    public boolean decrement(Statistic s) {
        return this.decrement(s,1);
    }

    public boolean decrement(Statistic s, int i) {
        if (this.get(s) - i < 0) {
            return false;
        }
        this.set(s, this.get(s) - i);
        return true;
    }

    public void set(Statistic s, int i) {
        this.map.put(s, i);
        this.player.setData("stats." + (s.getGame() == null ? "general" : s.getGame().getId()) + "." + s.getId(),i);
    }

    @Override
    public void load(List<Statistic> statList) {
        for (Statistic s : statList) {
            load(s);
        }
    }


    public void load(Statistic s) {
        if (s != null) {
            if (!map.containsKey(s)) {
                if (s.getGame() == null) {
                    this.map.put(s, player.getData().getInt("stats.general." + s.getId(),0));
                } else {
                    this.map.put(s, player.getData().getInt("stats." + s.getGame().getId() + "." + s.getId(),0));
                }
            }
        }
    }

    public ItemLore createDisplayLore(Game game) {
        ItemLore b = ItemLore.create();
        b.empty();
        for (Statistic s : Main.getStatistics(game)) {
            b.parameter(s.getName(),this.get(s));
        }
        return b;
    }
}
