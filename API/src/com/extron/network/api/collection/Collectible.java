package com.extron.network.api.collection;

import com.extron.network.api.inventory.base.ItemDisplayable;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;

public interface Collectible extends ItemDisplayable {

    CollectibleType getType();

    default void onRegister() {}

    default boolean obtained(ExtronPlayer p) {
        return p.foundCollectible(this);
    }

    default boolean isObtainable() {
        return true;
    }
}
