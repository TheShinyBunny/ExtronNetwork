package com.extron.network.api.scoreboard;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.AnimatedText;
import com.extron.network.api.utils.Reflection;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.Map;

public abstract class Scoreboard {

    private static Map<String,Scoreboard> instances = new HashMap<>();

    public abstract String getId();

    public abstract String getTitle();

    public abstract void addLines(ExtronPlayer p, ScoreLines lines);

    public DisplaySlot getDisplaySlot() {
        return DisplaySlot.SIDEBAR;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Scoreboard && ((Scoreboard) obj).getId().equalsIgnoreCase(getId());
    }

    public abstract AnimatedText getTitleAnimation();
}
