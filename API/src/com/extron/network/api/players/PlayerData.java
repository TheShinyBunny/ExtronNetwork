package com.extron.network.api.players;

import com.extron.network.api.Main;
import com.extron.network.api.data.JsonData;

public class PlayerData extends JsonData<ExtronPlayer> {

    private ExtronPlayer player;

    public PlayerData(ExtronPlayer p) {
        super(Main.getPlayersData(),"data");
        this.player = p;
    }

    @Override
    public ExtronPlayer getOwner() {
        return player;
    }

    @Override
    public void save() {
        System.out.println("saving " + player.getName());
        System.out.println(json.toString());
        table.set(colName,player,json.toString());
    }
}
