package com.extron.network.api.collection.kits;

import com.extron.network.api.collection.CollectibleType;
import com.extron.network.api.collection.GeneralCollectibleType;
import com.extron.network.api.collection.Selectable;
import com.extron.network.api.economy.ShopItem;
import com.extron.network.api.game.Game;
import com.extron.network.api.game.GameMode;
import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface Kit extends Selectable, ShopItem {

    Game getGame();

    GameMode getGameMode();

    void onSpawnWith(ExtronPlayer p);

    void addItems(Consumer<ExtronStack> c);

    default List<ExtronStack> getItems() {
        List<ExtronStack> list = new ArrayList<>();
        addItems(list::add);
        return list;
    }

    @Override
    default Material getIcon() {
        ExtronStack s = getFirstItem();
        return s == null ? Material.BARRIER : s.getType();
    }

    default ExtronStack getFirstItem() {
        List<ExtronStack> list = getItems();
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    @Override
    default int getIconDamage() {
        ExtronStack s = getFirstItem();
        return s == null ? 0 : s.getData();
    }

    @Override
    default String getDescription() {
        return "";
    }

    @Override
    default CollectibleType getType() {
        return GeneralCollectibleType.KIT;
    }

    @Override
    default boolean isUpgradable() {
        return false;
    }
}
