package com.extron.network.api.collection;

import com.extron.network.api.Main;
import com.extron.network.api.utils.ListUtils;

import java.util.List;

public interface LobbyCollectible extends Selectable, CategoryItem {

    Rarity getRarity();

    @Override
    LobbyCollectibleType getType();

    boolean foundInBasicLoot();

    @Override
    default Category getCategory() {
        return getType();
    }

    static List<LobbyCollectible> getAll() {
        return ListUtils.castAll(ListUtils.filter(Main.getCollectibles(),c->c instanceof LobbyCollectible),LobbyCollectible.class);
    }
}
