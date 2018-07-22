package com.extron.network.api.hologram;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.players.PlayerList;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VariabledHologram extends SavedHologram {
    public VariabledHologram(ExtronWorld world, Location loc, UUID id) {
        super(world, loc, id);
    }

    @Override
    public boolean spawn() {
        replaceVariables();
        return super.spawn();
    }

    private void replaceVariables() {
        List<String> lines = new ArrayList<>(getLines());
        getLines().clear();
        for (String line : lines) {
            line = line.replaceAll("<online_players>",String.valueOf(PlayerList.getOnlinePlayers().size()));
            line = line.replaceAll("<all_players>",String.valueOf(PlayerList.getAllPlayers().size()));
            getLines().add(line);
        }
    }
}
