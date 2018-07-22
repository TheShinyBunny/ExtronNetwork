package com.extron.network.api.economy;

import com.extron.network.api.collection.Category;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.players.ExtronPlayer;

import java.util.List;

public class AbstractShop implements Shop {
    @Override
    public Currency[] getCurrencies() {
        return new Currency[0];
    }

    @Override
    public void addItems() {

    }

    @Override
    public void addItem(ShopItem item) {

    }

    @Override
    public List<ShopItem> getAllItems() {
        return null;
    }

    @Override
    public List<ShopItem> getItemsInCategory(Category c) {
        return null;
    }

    @Override
    public InventoryMenu createMenu() {
        return null;
    }

    @Override
    public void onOpen(ExtronPlayer p) {

    }

    @Override
    public void onClose(ExtronPlayer p) {

    }

    @Override
    public void onBuyItem(ExtronPlayer p, ShopItem item) {

    }
}
