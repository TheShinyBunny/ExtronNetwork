package com.extron.network.api.collection.loot;

import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.JsonContainer;
import com.extron.network.api.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class LootInstance {

    private final LootBox box;
    private List<LobbyCollectible> awards;

    public LootInstance(LootBox box) {
        this.box = box;
        this.awards = new ArrayList<>();
    }

    public LootBox getLootBox() {
        return box;
    }

    public List<LobbyCollectible> getPossibleAwards() {
        return awards;
    }

    public JsonContainer toJson() {
        JsonContainer c = new JsonContainer();
        c.set("id",box.getId());
        c.set("rewards",ListUtils.convertAll(awards, LootBoxManager::saveAward));
        return c;
    }

    public void open(ExtronPlayer p) {
        LobbyCollectible lc = ListUtils.weightedRandomItem(awards,c->c.getRarity().getWeight());
        if (lc == null) {
            p.sendMessage("Can't generate random loot item.");
            return;
        }
        p.sendMessage("You found a " + lc.getRarity().name() + " " + lc.getDisplayName() + " in the " + box.getDisplayName());
        if (p.foundCollectible(lc)) {
            p.sendMessage("You had already found it!");
        }
        p.getCollection().add(lc);
        p.getCollection().removeLootBox(this);
    }
}
