package com.extron.utils.protocol.packets;

import org.bukkit.entity.Player;

public interface PacketWrapper {

    void send(Player p);

    boolean isValid();

}
