package com.extron.network.api.game;

import com.extron.network.api.players.ExtronPlayer;

import java.util.List;

public abstract class GameJoinException extends Exception {

    public static class PartyCantFit extends GameJoinException {

    }

    public static class PartyPlayersOffline extends GameJoinException{
        private final List<ExtronPlayer> players;

        public PartyPlayersOffline(List<ExtronPlayer> offline) {
            this.players = offline;
        }

        public List<ExtronPlayer> getPlayers() {
            return players;
        }
    }

    public static class PartiesNotAllowed extends GameJoinException {

    }

    public static class NotTheLeader extends GameJoinException {

    }

    public static class GameStarted extends GameJoinException {

    }

}