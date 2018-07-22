package com.extron.network.api.game.helpers;

import com.extron.network.api.inventory.base.ExtronStack;

public class LootEntry {

    private final LootGroup group;
    private ExtronStack stack;
    private int weight;

    public LootEntry(ExtronStack stack, int weight) {
        this(stack,weight,LootGroup.NONE);
    }

    public LootEntry(ExtronStack stack, int weight, LootGroup group) {
        this.stack = stack;
        this.weight = weight;
        this.group = group;
    }

    public LootGroup getGroup() {
        return group;
    }

    public ExtronStack getStack() {
        return stack;
    }

    public int getWeight() {
        return weight;
    }
}
