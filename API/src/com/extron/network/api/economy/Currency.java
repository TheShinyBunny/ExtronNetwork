package com.extron.network.api.economy;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

public interface Currency {

    String getName();

    ChatColor getColor();

    String getId();

    double of(ExtronPlayer p);

}
