package com.extron.network.api.game;

import com.extron.network.api.utils.ListUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameStartRules {

    private List<Hook> hooks;
    private int maxPlayers;
    private int maxPlayersCountdown;

    public GameStartRules() {
        this.hooks = new ArrayList<>();
    }

    public GameStartRules addPlayerHook(int minPlayers, int countdown) {
        this.hooks.add(new Hook(minPlayers,countdown));
        return this;
    }

    public GameStartRules setMaxPlayers(int maxPlayers, int maxPlayersCountdown) {
        this.maxPlayers = maxPlayers;
        this.maxPlayersCountdown = maxPlayersCountdown;
        return this;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        if (hooks.isEmpty()) return getMaxPlayers();
        List<Hook> newList = new ArrayList<>(hooks);
        newList.sort(Comparator.comparingInt(Hook::getPlayers));
        return newList.get(0).players;
    }

    public int getMinCountdown() {
        if (hooks.isEmpty()) return getMaxPlayersCountdown();
        List<Hook> newList = new ArrayList<>(hooks);
        newList.sort(Comparator.comparingInt(Hook::getCountdown));
        return newList.get(0).countdown;
    }

    public int getMaxPlayersCountdown() {
        return maxPlayersCountdown;
    }

    public Hook getMatchingHook(int players) {
        return ListUtils.firstMatch(hooks,h->h.players<=players);
    }

    public static class Hook {
        private int players;
        private int countdown;

        public Hook(int players, int countdown) {
            this.players = players;
            this.countdown = countdown;
        }

        public int getCountdown() {
            return countdown;
        }

        public int getPlayers() {
            return players;
        }
    }

    public static GameStartRules simple(int players, int countdown) {
        return new GameStartRules().setMaxPlayers(players,countdown);
    }

}
