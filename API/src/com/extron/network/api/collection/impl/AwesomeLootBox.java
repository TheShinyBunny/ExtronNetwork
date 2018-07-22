package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.loot.GameAwardBox;
import com.extron.network.api.collection.loot.LootTier;

public class AwesomeLootBox implements GameAwardBox {
    @Override
    public int getChance() {
        return 1;
    }

    @Override
    public LootTier getTier() {
        return LootTier.SOME_LEGENDARY;
    }

    @Override
    public boolean canContain(LobbyCollectible c) {
        return c.foundInBasicLoot();
    }

    @Override
    public String getId() {
        return "awesome";
    }

    @Override
    public String getDisplayName() {
        return "Awesome Loot Box";
    }

    @Override
    public String getDescription() {
        return "It's so awesome!";
    }
}
