package com.extron.network.api.players;

import com.extron.network.api.Main;
import com.extron.network.api.scoreboard.MainScoreboard;
import com.extron.network.api.scoreboard.Scoreboard;
import com.extron.network.api.scoreboard.ScoreboardUpdater;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.punishment.PunishType;
import com.extron.network.api.utils.punishment.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class PlayerList implements ScoreboardUpdater {

    private static boolean INITIALIZED = false;
    private static Set<ExtronPlayer> allPlayers;
    private static Set<ExtronPlayer> onlinePlayers;
    private static Set<ExtronPlayer> bannedPlayers;
    private static Set<ExtronPlayer> mutedPlayers;

    private static PlayerList INSTANCE;

    static {
        allPlayers = new HashSet<>();
        onlinePlayers = new HashSet<>();
        bannedPlayers = new HashSet<>();
        mutedPlayers = new HashSet<>();
    }

    // The PlayerList class should not be instantiated from outside.
    private PlayerList() {}

    public static void playerJoined(ExtronPlayer p) {
        allPlayers.add(p);
        onlinePlayers.add(p);
        p.join();
        PacketListener.set(p);
        onPlayersChanged();
    }

    public static void playerLeft(ExtronPlayer p) {
        onlinePlayers.remove(p);
        p.leave();
        onPlayersChanged();
    }

    public static Set<ExtronPlayer> getAllPlayers() {
        return allPlayers;
    }

    public static Set<ExtronPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    public static Collection<String> getOnlinePlayerNames() {
        return ListUtils.convertAll(onlinePlayers,ExtronPlayer::getName);
    }

    public static Collection<String> getAllPlayerNames() {
        return ListUtils.convertAll(allPlayers,ExtronPlayer::getName);
    }

    public static void forEachOnline(Consumer<ExtronPlayer> func) {
        onlinePlayers.forEach(func);
    }

    public static ExtronPlayer getPlayer(OfflinePlayer player) {
        return getPlayer(player.getUniqueId());
    }

    private static ExtronPlayer getPlayer(UUID uuid) {
        return ListUtils.firstMatch(allPlayers,pl->pl.getUUID().equals(uuid));
    }

    public static boolean punish(Punishment punishment) {
        boolean b = punishment.getType() == PunishType.BAN ? bannedPlayers.contains(punishment.getPlayer()) : mutedPlayers.contains(punishment.getPlayer());
        if (punishment.getType() == PunishType.BAN) {
            bannedPlayers.add(punishment.getPlayer());
            punishment.getPlayer().setBan(punishment);
        } else {
            mutedPlayers.add(punishment.getPlayer());
            punishment.getPlayer().setMute(punishment);
        }
        punishment.getPlayer().saveData();
        return b;
    }

    public static ExtronPlayer getOnlinePlayer(String name) {
        return ListUtils.firstMatch(onlinePlayers,p->p.getName().equalsIgnoreCase(name));
    }

    public static ExtronPlayer create(Player player) {
        ExtronPlayer p = new ExtronPlayer((CraftPlayer) player);
        allPlayers.add(p);
        Main.getDatabaseManager().createPlayerData(p);
        return p;
    }

    public static void init() {
        if (INITIALIZED) return;
        INITIALIZED = true;
        INSTANCE = new PlayerList();
        for (String u : Main.getDatabaseManager().getAllPlayerUUIDs()) {
            ExtronPlayer p = null;
            UUID uuid = UUID.fromString(u);
            if (Bukkit.getPlayer(uuid) == null) {
                if (Bukkit.getOfflinePlayer(uuid).getName() != null) {
                    p = new ExtronPlayer((CraftOfflinePlayer) Bukkit.getOfflinePlayer(uuid));
                }
            } else {
                p = new ExtronPlayer((CraftPlayer) Bukkit.getPlayer(uuid));
                onlinePlayers.add(p);
            }
            if (p == null) return;
            Main.getDatabaseManager().loadPlayerData(p);
            p.init();
            allPlayers.add(p);

            if (p.isBanned()) {
                bannedPlayers.add(p);
            }
            if (p.isMuted()) {
                mutedPlayers.add(p);
            }
        }
    }

    public static void onPlayersChanged() {
        INSTANCE.updateScoreboard();
    }

    public static Set<ExtronPlayer> getBannedPlayers() {
        return bannedPlayers;
    }

    public static boolean unban(ExtronPlayer p) {
        if (p.isBanned()) {
            p.getBan().remove();
            bannedPlayers.remove(p);
            p.saveData();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldUpdate(Scoreboard sb) {
        return sb instanceof MainScoreboard;
    }

    @Override
    public void update(Scoreboard sb) {
        Main.updateScoreboard(sb);
    }
}
