package com.extron.network.api.players;

import com.extron.network.api.permission.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

public class FakePlayer extends ExtronPlayer {
    public final CommandSender sender;

    public FakePlayer(CraftPlayer handle, CommandSender sender) {
        super(handle);
        this.sender = sender;
    }

    public static FakePlayer of(CommandSender sender) {
        return new FakePlayer(null,sender);
    }

    @Override
    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    @Override
    public void sendMessage(String msg, Object... formatArgs) {
        this.sendMessage(String.format(msg,formatArgs));
    }

    @Override
    public Rank getRank() {
        return Rank.DEVELOPER;
    }
}
