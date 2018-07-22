package com.extron.network.api.nick;

import com.extron.network.api.permission.Rank;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.Reflection;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R1.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;

public class NickHelper {

    public static boolean nickPlayer(ExtronPlayer sender, String name) {
        Rank rank;
        if (sender.isNicked()) {
            rank = sender.getNickRank();
        } else {
             rank = getRandomValidRank();
        }
        if (rank == null) return false;
        nickPlayer(sender,name,rank,true);
        return true;
    }

    public static void nickPlayer(ExtronPlayer player, String name, Rank rank, boolean updateNickManager) {
        if (updateNickManager) {
            player.getNickManager().nick(name, rank);
        }
        if (player.handle != null) {
            updateName(player,name);
        }
    }

    public static void setNameAndSkin(ExtronPlayer player, String name, String skin) {
        GameProfile profile = player.getProfile();
        if (profile != null) {
            Reflection.setValue(profile,"name",name);

        }
        refreshPlayer(player);
    }

    public static void refreshPlayer(ExtronPlayer player) {
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            p.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,player.getNMS()));
            p.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER,player.getNMS()));
            if (p != player) {
                p.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityID()));
                p.sendPacket(new PacketPlayOutNamedEntitySpawn(player.getNMS()));
            }
        }
    }

    public static Rank getRandomValidRank() {
        return ListUtils.randomItem(Rank.ALL,Rank::isNickable);
    }


    public static void updateName(ExtronPlayer player, String name) {
        setNameAndSkin(player,name,"Steve");
        player.handle.setDisplayName(player.getDisplayName());
        player.handle.setPlayerListName(player.getDisplayName());
        PlayerNick.update(player);
    }

    public static void setSkin(ExtronPlayer p, String name) {

    }

    public static boolean isNameTaken(String name) {
        for (ExtronPlayer p : PlayerList.getAllPlayers()) {
            if (p.getNickName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static void setRank(ExtronPlayer player, Rank r) {
        player.getNickManager().setNickRank(r);
    }

    public static void unnick(ExtronPlayer player) {
        nickPlayer(player,player.getName(),player.getRank(),false);
        player.getNickManager().unnick();
        player.handle.setPlayerListName(player.getRealDisplayName());
    }
}
