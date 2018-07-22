package com.extron.network.api.economy;

import com.extron.network.api.collection.Category;
import com.extron.network.api.inventory.base.ItemDisplayable;
import com.extron.network.api.players.ExtronPlayer;

public interface ShopItem extends ItemDisplayable {

    boolean isUpgradable();

    void onPurchase(ExtronPlayer p);

    boolean canBuy(ExtronPlayer p);

    double getCost(ExtronPlayer p);

    Currency getCurrency();

    Category getCategory();

    int getBuyCooldown();

}
