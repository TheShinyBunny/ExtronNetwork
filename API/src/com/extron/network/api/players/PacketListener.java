package com.extron.network.api.players;

import com.extron.network.api.Main;
import net.minecraft.server.v1_8_R1.*;

import java.util.List;

public class PacketListener extends PlayerConnection {
    private final ExtronPlayer extronPlayer;

    public PacketListener(ExtronPlayer p, MinecraftServer minecraftserver, NetworkManager networkmanager) {
        super(minecraftserver, networkmanager, p.getNMS());
        this.extronPlayer = p;
    }

    public static void set(ExtronPlayer p) {
        new PacketListener(p,MinecraftServer.getServer(),p.getConnection().networkManager);
    }

    @Override
    public void a(PacketPlayInTabComplete packet) {
        List<String> list = Main.getCommandManager().tabComplete(extronPlayer,packet.a());
        list.addAll(MinecraftServer.getServer().tabCompleteCommand(extronPlayer.getNMS(),packet.a(),packet.b()));
        this.sendPacket(new PacketPlayOutTabComplete(list.toArray(new String[0])));
    }
}
