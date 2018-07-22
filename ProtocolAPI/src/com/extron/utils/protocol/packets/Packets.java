package com.extron.utils.protocol.packets;

import com.extron.utils.protocol.reflection.Reflection;

public class Packets<T extends PacketWrapper> {
    public static final Packets<PacketOutPlayerInfo> PLAYER_INFO = new Packets<>(PacketOutPlayerInfo.class);

    private final Class<T> packetClass;

    private Packets(Class<T> packetClass) {
        this.packetClass = packetClass;
    }

    public T create() {
        return Reflection.newInstance(packetClass,new Class[0]);
    }

}
