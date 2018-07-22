package com.extron.network.cards;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.config.Config;
import com.extron.network.api.game.GameSettings;
import com.extron.network.api.game.Team;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

public class CardGameManager extends GameManager {
    private final CardMinigame minigame;

    public CardGameManager(CardMinigame minigame, ExtronWorld map, GameSettings settings) {
        super(map,settings);
        this.minigame = minigame;
    }

    @Override
    protected void onCountdownFinished() {
        start();
    }

    @Override
    protected void startingIn(int seconds) {
        if (seconds % 10 == 0 || seconds < 6) {
            messageAll(waiting, ChatColor.YELLOW + "The game starts in " + numberChatColor(seconds, false) + seconds + " " + ChatColor.YELLOW + TextUtils.addNeededS(seconds, "second") + "!");
            if (seconds % 10 == 0) {
                titleAll(waiting,ChatColor.AQUA + "Starting In",numberChatColor(seconds,true) + "" + seconds);
            } else {
                titleAll(waiting,ChatColor.AQUA + "Starting In",numberChatColor(seconds,true) + "" + seconds, 0,23,0);
            }
        }
        updateAllScoreboards(waiting);
    }

    private ChatColor numberChatColor(int seconds, boolean title) {
        if (title) {
            return seconds % 10 == 0 ? ChatColor.GREEN : seconds < 6 && seconds > 2 ? ChatColor.YELLOW : ChatColor.RED;
        } else {
            return seconds % 10 == 0 ? ChatColor.GOLD : ChatColor.RED;
        }
    }

    @Override
    protected void sendJoinMessage(ExtronPlayer p, int players, int maxPlayers) {
        messageAll(waiting,p.getDisplayName() + ChatColor.GREEN + " wants to play some Taki!");
    }

    @Override
    public boolean isGoingForever() {
        return false;
    }

    @Override
    public void end() {

    }

    @Override
    public void sendMessage(ExtronPlayer p, String message) {

    }

    @Override
    public void setupPlayerOnStart(ExtronPlayer p) {

    }

    @Override
    protected void sendPreGameQuitMessage(ExtronPlayer p) {

    }

    @Override
    protected void sendDeathTitle(ExtronPlayer p) {

    }

    @Override
    protected void sendFinalDeathTitle(ExtronPlayer p) {

    }

    @Override
    protected boolean isFinalKill(ExtronPlayer p) {
        return false;
    }

    @Override
    protected void setSpectator(ExtronPlayer p) {

    }

    @Override
    protected boolean canPlayerRejoin(ExtronPlayer p) {
        return false;
    }

    @Override
    protected void loadFromConfig(Config config) {

    }

    @Override
    protected void sendTeamEliminatedMessage(Team team) {

    }
}
