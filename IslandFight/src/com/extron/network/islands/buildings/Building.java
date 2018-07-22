package com.extron.network.islands.buildings;

import com.extron.network.api.inventory.base.ItemDisplayable;
import org.bukkit.Location;

public interface Building extends ItemDisplayable {

    boolean isBasic();

    boolean isImportant();

    void build(Location loc);

    int getMaxLevel();

}
