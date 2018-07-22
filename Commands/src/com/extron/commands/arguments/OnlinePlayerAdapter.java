package com.extron.commands.arguments;

import com.extron.commands.InvalidArgumentException;
import com.extron.commands.dispatcher.ArgumentAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OnlinePlayerAdapter implements ArgumentAdapter<Player> {
    @Override
    public Class<Player> getType() {
        return Player.class;
    }

    @Override
    public Player parse(String input, String argName) throws InvalidArgumentException {
        Player p = Bukkit.getPlayer(input);
        if (p == null) {
            if (Bukkit.getOfflinePlayer(input) != null) {
                throw InvalidArgumentException.offlinePlayer(input);
            }
            throw InvalidArgumentException.unknown(argName,input);
        }
        return p;
    }

    @Nullable
    @Override
    public Player defaultsTo() {
        return null;
    }

    @Override
    public Collection<String> getArgCompletions(Parameter p) {
        List<String> list = new ArrayList<>();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            list.add(pl.getName());
        }
        return list;
    }
}
