package com.extron.network.api.game;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

public enum DeathCause {
    PLAYER_KILLED,
    ENTITY_KILLED,
    VOID,
    LEAVE,
    FALL,
    EXPLODED,
    LAVA,
    FIRE,
    POTION,
    FALL_KILL,
    VOID_KILL,
    EXPLODE_KILL,
    CACTUS,
    SUFFOCATE,
    ENTITY_SHOOT,
    PLAYER_SHOOT,
    DROWN,
    LIGHTNING,
    STARVE,
    WITHER,
    FALLING_BLOCK,
    THORNS,
    UNKNOWN;


    public static DeathCause from(EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case CONTACT:
                return CACTUS;
            case ENTITY_ATTACK:
                return ENTITY_KILLED;
            case PROJECTILE:
                return ENTITY_SHOOT;
            case SUFFOCATION:
                return SUFFOCATE;
            case FALL:
                return FALL;
            case FIRE:
            case FIRE_TICK:
            case MELTING:
                return FIRE;
            case LAVA:
                return LAVA;
            case DROWNING:
                return DROWN;
            case BLOCK_EXPLOSION:
                return EXPLODED;
            case ENTITY_EXPLOSION:
                return EXPLODE_KILL;
            case VOID:
                return VOID;
            case LIGHTNING:
                return LIGHTNING;
            case SUICIDE:
                break;
            case STARVATION:
                return STARVE;
            case POISON:
                return POTION;
            case MAGIC:
                return POTION;
            case WITHER:
                return WITHER;
            case FALLING_BLOCK:
                return FALLING_BLOCK;
            case THORNS:
                return THORNS;
            case CUSTOM:
                break;
        }
        return UNKNOWN;
    }

    public static DeathCause from(ExtronPlayer p, EntityDamageEvent.DamageCause cause, EntityDamageEvent.DamageCause directCause) {
        switch (directCause) {
            case VOID:
                if (isAttack(cause)) {
                    return VOID_KILL;
                }
                return VOID;
            case FALL:
                if (isAttack(cause)) {
                    return FALL_KILL;
                }
                return FALL;
            case ENTITY_ATTACK:
                if (p.getLastDamager() instanceof Player) {
                    return PLAYER_KILLED;
                }
                return ENTITY_KILLED;
            case PROJECTILE:
                if (p.getLastDamager() instanceof Projectile) {
                    if (((Projectile) p.getLastDamager()).getShooter() instanceof Player) {
                        return PLAYER_SHOOT;
                    }
                    return ENTITY_SHOOT;
                }
                break;
        }
        return UNKNOWN;
    }

    private static boolean isAttack(EntityDamageEvent.DamageCause cause) {
        return cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.PROJECTILE;
    }
}
