package com.extron.network.api.collection.loot;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public enum LootTier {
    NO_LEGENDARY(3,2,1,0), BASIC(4,4,3,1), RARE(4,5,3,1), SOME_LEGENDARY(2,3,3,3), ALL_LEGENDARY(0,0,0,1);

    private final int common;
    private final int rare;
    private final int epic;
    private final int legendary;

    LootTier(int common, int rare, int epic, int legendary) {
        this.common = common;
        this.rare = rare;
        this.epic = epic;
        this.legendary = legendary;
    }

    public List<LobbyCollectible> generateRewards(LootBox box) {
        List<LobbyCollectible> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(randomReward(box));
        }
        return list;
    }

    public int getRarityWeight(Rarity r) {
        switch (r) {
            case COMMON:
                return common;
            case EPIC:
                return epic;
            case LEGENDARY:
                return legendary;
            case RARE:
                return rare;
        }
        return 0;
    }

    public LobbyCollectible randomReward(LootBox box) {
        Rarity r = ListUtils.weightedRandomItem(ListUtils.enumList(Rarity.class),this::getRarityWeight);
        List<LobbyCollectible> list = ListUtils.filter(LobbyCollectible.getAll(),c-> c.getRarity() == r);
        return ListUtils.randomItem(list, c->c.isObtainable() && box.canContain(c));
    }
}
