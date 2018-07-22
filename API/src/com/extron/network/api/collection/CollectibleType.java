package com.extron.network.api.collection;

import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public interface CollectibleType extends Category{

    void select(ExtronPlayer player, Selectable c);

    void deselect(ExtronPlayer player);

    void menuClick(Collectible c, ExtronPlayer player, ButtonClickEvent click);

}
