package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.CollectibleType;
import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.loot.GameAwardBox;
import com.extron.network.api.collection.loot.LootTier;

public class CommonLootBox implements GameAwardBox {
    @Override
    public int getChance() {
        return 10;
    }

    @Override
    public LootTier getTier() {
        return LootTier.NO_LEGENDARY;
    }

    @Override
    public boolean canContain(LobbyCollectible c) {
        return c.getRarity() != Rarity.LEGENDARY;
    }

    @Override
    public String getId() {
        return "common";
    }

    @Override
    public String getDisplayName() {
        return "Common Loot Box";
    }

    @Override
    public String getDescription() {
        return "its common yay";
    }


}
