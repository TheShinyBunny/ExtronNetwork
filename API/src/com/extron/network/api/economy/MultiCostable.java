package com.extron.network.api.economy;

import com.extron.network.api.players.ExtronPlayer;

public interface MultiCostable {

    double getCost(ExtronPlayer p, Currency c);

    Currency[] getCurrencies();

}
