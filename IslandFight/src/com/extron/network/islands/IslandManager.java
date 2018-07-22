package com.extron.network.islands;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.*;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.party.Party;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class IslandManager implements IGameManager {

    @Override
    public void tryJoinPlayer(ExtronPlayer p) throws GameJoinException {

    }

    @Override
    public void tryJoinParty(Party p) throws GameJoinException {

    }

    @Override
    public void start() {

    }

    @Override
    public boolean isGoingForever() {
        return true;
    }

    @Override
    public ExtronWorld getMap() {
        return null;
    }

    @Override
    public GameState getState() {
        return null;
    }

    @Override
    public void setState(GameState state) {

    }

    @Override
    public GameMode getGameMode() {
        return null;
    }

    @Override
    public int getOpenSlotsToJoin() {
        return 0;
    }

    @Override
    public void onPlayerJoined(ExtronPlayer p) {

    }

    @Override
    public void end() {

    }

    @Override
    public void onPlayerLeave(ExtronPlayer p) {

    }

    @Override
    public List<ExtronPlayer> getAllPlayers() {
        return null;
    }

    @Override
    public List<ExtronPlayer> getWaiting() {
        return null;
    }

    @Override
    public List<ExtronPlayer> getSpectators() {
        return null;
    }

    @Override
    public void sendMessage(ExtronPlayer p, String message) {

    }

    @Override
    public Death createDeathFromEvent(PlayerDeathEvent e) {
        return null;
    }

    @Override
    public void onPlayerDeath(Death death) {

    }

    @Override
    public GameSettings getSettings() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void forceStart(ExtronPlayer p) {

    }

    @Override
    public boolean isIngame() {
        return true;
    }

    @Override
    public void setupPlayerOnStart(ExtronPlayer p) {

    }

    @Override
    public int getStartCountdown() {
        return 0;
    }
}
