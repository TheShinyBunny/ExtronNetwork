package com.extron.network.api.collection.loot;

import org.bukkit.Material;

public interface GameAwardBox extends LootBox {

    @Override
    default Material getIcon() {
        return Material.CHEST;
    }

    @Override
    default int getIconDamage() {
        return 0;
    }
}
