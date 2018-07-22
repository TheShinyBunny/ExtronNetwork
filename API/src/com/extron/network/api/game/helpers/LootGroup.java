package com.extron.network.api.game.helpers;

public enum LootGroup {
    BLOCKS(2,1,0.5), WEAPONS(1,1,0.8), ARMOR(3,1,2), BOW(0,2,0.2), POTIONS(2,1,0.3), TOOLS(1,2,0.2), UTILS(1,0,0.4), NONE(1,0,1.0);

    private final int min;
    private final int additional;
    private final double extra;

    LootGroup(int min, int additional, double extraPlayerChance) {
        this.min = min;
        this.additional = additional;
        this.extra = extraPlayerChance;
    }

    public int getAdditional() {
        return additional;
    }

    public double getExtra() {
        return extra;
    }

    public int getMin() {
        return min;
    }
}
