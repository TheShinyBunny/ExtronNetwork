package com.extron.network.api.game.managers;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.*;
import com.extron.network.api.party.Party;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public interface IGameManager {

    void tryJoinPlayer(ExtronPlayer p) throws GameJoinException;

    void tryJoinParty(Party p) throws GameJoinException;

    void start();

    boolean isGoingForever();

    ExtronWorld getMap();

    GameState getState();

    void setState(GameState state);

    GameMode getGameMode();

    int getOpenSlotsToJoin();

    void onPlayerJoined(ExtronPlayer p);

    void end();

    void onPlayerLeave(ExtronPlayer p);

    List<ExtronPlayer> getAllPlayers();

    List<ExtronPlayer> getWaiting();

    List<ExtronPlayer> getSpectators();

    void sendMessage(ExtronPlayer p, String message);

    Death createDeathFromEvent(PlayerDeathEvent e);

    void onPlayerDeath(Death death);

    GameSettings getSettings();

    void dispose();

    void forceStart(ExtronPlayer p);

    boolean isIngame();

    void setupPlayerOnStart(ExtronPlayer p);

    int getStartCountdown();
}
