package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.loot.LootBox;
import com.extron.network.api.collection.loot.LootTier;
import org.bukkit.Material;

public class SpecialLootBox implements LootBox {


    @Override
    public int getChance() {
        return 0;
    }

    @Override
    public LootTier getTier() {
        return LootTier.ALL_LEGENDARY;
    }

    @Override
    public boolean canContain(LobbyCollectible c) {
        return c.getRarity() == Rarity.LEGENDARY && !c.foundInBasicLoot();
    }

    @Override
    public String getId() {
        return "special";
    }

    @Override
    public String getDisplayName() {
        return "Special Loot Box";
    }

    @Override
    public Material getIcon() {
        return Material.ENDER_CHEST;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "its special";
    }
}
