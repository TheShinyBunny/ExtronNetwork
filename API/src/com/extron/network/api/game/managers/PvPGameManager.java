package com.extron.network.api.game.managers;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.game.Death;
import com.extron.network.api.game.GameSettings;
import com.extron.network.api.game.Team;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class PvPGameManager extends CompetitiveManager implements IPvPManager {

    protected Map<ExtronPlayer, Integer> kills;
    protected Map<ExtronPlayer, Integer> deaths;

    public PvPGameManager(ExtronWorld map, GameSettings settings) {
        super(map, settings);
        kills = new HashMap<>();
        deaths = new HashMap<>();
    }

    @Override
    public int getKillsOf(ExtronPlayer p) {
        return kills.getOrDefault(p,0);
    }

    @Override
    public int getDeathsOf(ExtronPlayer p) {
        return deaths.getOrDefault(p,0);
    }

    @EventHandler
    private void damage(EntityDamageByEntityEvent e) {
        System.out.println("entity damage by entity event!");
        if (e.getDamager() instanceof Player) {
            ExtronPlayer attacker = ExtronPlayer.of((Player) e.getDamager());
            if (e.getEntity() instanceof Player) {
                ExtronPlayer target = ExtronPlayer.of((Player) e.getEntity());
                onPlayerAttack(attacker,target,e);
            }
        }
    }

    @Override
    public void onPlayerAttack(ExtronPlayer attacker, ExtronPlayer target, EntityDamageByEntityEvent e) {
        if (areOnSameTeam(attacker,target)) {
            e.setCancelled(true);
        } else if (isSpectator(attacker) || isSpectator(target)) {
            e.setCancelled(true);
        }
    }

    @Override
    public void start() {
        super.start();
        for (ExtronPlayer p : alivePlayers) {
            p.invulnerable = setInvulnerableOnStart();
        }
    }

    public boolean setInvulnerableOnStart() {
        return false;
    }

    @Override
    public void onPlayerDeath(Death death) {
        super.onPlayerDeath(death);
        if (!getState().isAfterGame()) {
            if (death.getKiller() instanceof Player) {
                ExtronPlayer killer = ExtronPlayer.of((Player)death.getKiller());
                kills.put(killer,kills.getOrDefault(killer,0)+1);
            }
            deaths.put(death.getPlayer(),deaths.getOrDefault(death.getPlayer(),0)+1);
        }
        updateAllScoreboards(allPlayers);
    }

    public void printTeamAndTopKills(Team t) {

    }
}
