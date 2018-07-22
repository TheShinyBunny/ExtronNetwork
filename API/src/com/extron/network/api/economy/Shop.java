package com.extron.network.api.economy;

import com.extron.network.api.collection.Category;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.players.ExtronPlayer;

import java.util.List;

public interface Shop {

    Currency[] getCurrencies();

    void addItems();

    void addItem(ShopItem item);

    List<ShopItem> getAllItems();

    List<ShopItem> getItemsInCategory(Category c);

    InventoryMenu createMenu();

    void onOpen(ExtronPlayer p);

    void onClose(ExtronPlayer p);

    void onBuyItem(ExtronPlayer p, ShopItem item);

}
