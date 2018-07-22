package com.extron.network.api.economy;

import com.extron.network.api.players.ExtronPlayer;

import java.util.HashMap;
import java.util.Map;

public class PurchaseRequest {

    private Shop shop;
    private ExtronPlayer player;
    private Map<Currency,Double> costsMap;

    public PurchaseRequest(Shop shop, ExtronPlayer p) {
        this.shop = shop;
        this.player = p;
        this.costsMap = new HashMap<>();
    }

    public PurchaseRequest(Shop shop, ExtronPlayer player, Currency currency) {
        this.shop = shop;
        this.player = player;
        this.costsMap = new HashMap<>();
        costsMap.put(currency,0.0);
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public Shop getShop() {
        return shop;
    }

    public void setCost(double cost) {
        if (!costsMap.isEmpty()) {
            costsMap.keySet().stream().findFirst().ifPresent(c -> costsMap.put(c, cost));
        }
    }
}
