package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.loot.GameAwardBox;
import com.extron.network.api.collection.loot.LootTier;

public class AdvancedLootBox implements GameAwardBox {
    @Override
    public int getChance() {
        return 3;
    }

    @Override
    public LootTier getTier() {
        return LootTier.RARE;
    }

    @Override
    public boolean canContain(LobbyCollectible c) {
        return c.foundInBasicLoot();
    }

    @Override
    public String getId() {
        return "advanced";
    }

    @Override
    public String getDisplayName() {
        return "Advanced Loot Box";
    }

    @Override
    public String getDescription() {
        return "yo";
    }
}
