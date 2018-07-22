package com.extron.network.api.game;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.entity.Entity;

public class Death {

    private ExtronPlayer player;
    private DeathCause cause;
    private Entity killer;
    private ExtronPlayer assisted;

    public Death(ExtronPlayer player, DeathCause cause, Entity killer, ExtronPlayer assisted) {
        this.player = player;
        this.cause = cause;
        this.killer = killer;
        this.assisted = assisted;
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public DeathCause getCause() {
        return cause;
    }

    public Entity getKiller() {
        return killer;
    }

    public ExtronPlayer getAssisted() {
        return assisted;
    }
}
