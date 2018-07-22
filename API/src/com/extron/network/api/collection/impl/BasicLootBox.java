package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.loot.GameAwardBox;
import com.extron.network.api.collection.loot.LootTier;

public class BasicLootBox implements GameAwardBox {
    @Override
    public int getChance() {
        return 5;
    }

    @Override
    public LootTier getTier() {
        return LootTier.BASIC;
    }

    @Override
    public boolean canContain(LobbyCollectible c) {
        return c.foundInBasicLoot();
    }

    @Override
    public String getId() {
        return "basic";
    }

    @Override
    public String getDisplayName() {
        return "Basic Loot Box";
    }

    @Override
    public String getDescription() {
        return "descriptions are useless here honestly";
    }
}
