package com.extron.network.api.game.managers;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface IPvPManager extends ICompetitiveManager {

    int getKillsOf(ExtronPlayer p);

    int getDeathsOf(ExtronPlayer p);

    boolean isFinalKill(ExtronPlayer p);

    void onPlayerAttack(ExtronPlayer attacker, ExtronPlayer target, EntityDamageByEntityEvent e);

}
