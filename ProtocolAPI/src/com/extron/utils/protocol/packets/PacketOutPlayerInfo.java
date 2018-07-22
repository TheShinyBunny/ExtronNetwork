package com.extron.utils.protocol.packets;

import com.extron.utils.protocol.reflection.Reflection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketOutPlayerInfo implements PacketWrapper {

    private PlayerInfoAction action;
    private List<Player> players;

    public PacketOutPlayerInfo() {
        players = new ArrayList<>();
    }

    @Override
    public void send(Player p) {
        //Reflection.sendPacket(p,"PacketPlayOutPlayerInfo",new String[]{Reflection.nmsClass("EnumPlayerInfoAction"),Reflection.nmsClassArray("EntityPlayer")},action,players.toArray());
    }

    @Override
    public boolean isValid() {
        return action != null && !players.isEmpty();
    }

    public PacketOutPlayerInfo setAction(PlayerInfoAction action) {
        this.action = action;
        return this;
    }

    public PacketOutPlayerInfo addPlayer(Player p) {
        this.players.add(p);
        return this;
    }

    public PacketOutPlayerInfo addPlayers(Player... ps) {
        this.players.addAll(Arrays.asList(ps));
        return this;
    }

}
