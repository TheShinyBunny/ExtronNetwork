package com.extron.network.api.collection;

import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;

public enum Rarity {
    COMMON(10,ChatColor.GREEN),
    RARE(5,ChatColor.BLUE),
    EPIC(2,ChatColor.LIGHT_PURPLE),
    LEGENDARY(1,ChatColor.GOLD);

    private final int weight;
    private ChatColor color;

    Rarity(int weight, ChatColor color) {
        this.weight = weight;
        this.color = color;
    }

    public int getWeight() {
        return weight;
    }

    public static Rarity random() {
        return ListUtils.weightedRandomItem(ListUtils.enumList(Rarity.class),Rarity::getWeight);
    }

    public ChatColor getColor() {
        return color;
    }
}
