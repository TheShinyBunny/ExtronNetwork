package com.extron.network.api.scoreboard;

import com.extron.network.api.Main;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScoreLines implements Iterable<String> {

    public static final int MAX_LENGTH = 15;

    private ExtronPlayer player;
    private List<String> lines;
    private int length;
    private int emptyCount;

    public ScoreLines(ExtronPlayer p) {
        this.player = p;
        this.lines = new ArrayList<>();
        this.length = 0;
        this.emptyCount = 0;
    }

    public void add(String s) {
        if (checkLength()) {
            this.lines.add(s);
        }
    }

    public void add(Object obj) {
        this.add(String.valueOf(obj));
    }

    private boolean checkLength() {
        if (length < MAX_LENGTH) {
            length++;
            return true;
        }
        return false;
    }

    public void addParam(String name, Object value) {
        this.add(name + ": " + ChatColor.GREEN + value);
    }

    public void addEmpty() {
        StringBuilder res = new StringBuilder();
        emptyCount++;
        for (int i = 0; i < emptyCount; i++) {
            res.append(ChatColor.RESET.toString());
        }
        this.add(res);
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public int size() {
        return length;
    }

    @Nonnull
    @Override
    public Iterator<String> iterator() {
        return lines.iterator();
    }

    public void addWebsite() {
        this.add(ChatColor.YELLOW + Main.WEBSITE_ADDRESS);
    }
}
