package com.extron.network.api.game;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public interface DeathMessages {

    String createDeathMessage(DeathCause cause, String playerName, @Nullable String entityName);

}
