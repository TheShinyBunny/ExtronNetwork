package com.extron.network.api.collection.loot;

import com.extron.network.api.collection.Collectible;
import com.extron.network.api.collection.CollectibleType;
import com.extron.network.api.collection.GeneralCollectibleType;
import com.extron.network.api.collection.LobbyCollectible;

public interface LootBox extends Collectible {

    int getChance();

    LootTier getTier();

    boolean canContain(LobbyCollectible c);

    @Override
    default CollectibleType getType() {
        return GeneralCollectibleType.LOOT_BOX;
    }
}
