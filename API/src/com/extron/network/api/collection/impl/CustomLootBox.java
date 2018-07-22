package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.loot.LootBox;
import com.extron.network.api.collection.loot.LootTier;
import org.bukkit.Material;

public class CustomLootBox implements LootBox {
    @Override
    public int getChance() {
        return 0;
    }

    @Override
    public LootTier getTier() {
        return LootTier.BASIC;
    }

    @Override
    public boolean canContain(LobbyCollectible c) {
        return true;
    }

    @Override
    public String getId() {
        return "custom";
    }

    @Override
    public String getDisplayName() {
        return "Custom Loot Box";
    }

    @Override
    public Material getIcon() {
        return Material.COMMAND;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "A custom loot box!";
    }
}
