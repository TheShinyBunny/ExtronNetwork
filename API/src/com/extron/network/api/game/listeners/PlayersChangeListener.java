package com.extron.network.api.game.listeners;

import com.extron.network.api.party.Party;
import com.extron.network.api.players.ExtronPlayer;

public interface PlayersChangeListener extends GameListener {

    void onPlayerJoined(ExtronPlayer p);

    void onPartyJoined(Party p);

    void onPlayerDeath(ExtronPlayer p);

    void onPlayerLeave(ExtronPlayer p);
}
