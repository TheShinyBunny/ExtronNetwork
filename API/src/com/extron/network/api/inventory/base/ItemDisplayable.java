package com.extron.network.api.inventory.base;

import org.bukkit.Material;

public interface ItemDisplayable {

    String getId();

    String getDisplayName();

    Material getIcon();

    int getIconDamage();

    String getDescription();

}
