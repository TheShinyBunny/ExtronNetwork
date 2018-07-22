package com.extron.network.api.collection.loot;

import com.extron.network.api.Main;
import com.extron.network.api.collection.Collectible;
import com.extron.network.api.collection.CollectibleType;
import com.extron.network.api.collection.LobbyCollectible;
import com.extron.network.api.collection.impl.CustomLootBox;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.JsonContainer;
import com.extron.network.api.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootBoxManager {

    private static List<LootBox> lootBoxes = new ArrayList<>();
    private static CustomLootBox customLootBox = new CustomLootBox();

    public static void addLootBox(LootBox lb) {
        lootBoxes.add(lb);
    }

    public static LootInstance createFromJson(JsonContainer c) {
        LootBox lb = getLootBox(c.getString("id"));
        if (lb == null) return null;
        LootInstance instance = new LootInstance(lb);
        for (JsonContainer r : c.getObjectList("rewards",new ArrayList<>())) {
            String id = r.getString("id");
            CollectibleType type = Main.getCollectibleType(r.getString("type"));
            Collectible c2 = Main.getCollectible(id,type);
            if (c2 instanceof LobbyCollectible) {
                instance.getPossibleAwards().add((LobbyCollectible) c2);
            }
        }
        return instance;
    }

    public static LootBox getLootBox(String id) {
        return ListUtils.firstMatch(lootBoxes,lb->lb.getId().equalsIgnoreCase(id));
    }

    public static void tryGenerateBox(ExtronPlayer p) {
        if (new Random().nextInt(20) == 0) {
            createBox(p);
        }
    }

    public static void createBox(ExtronPlayer p) {
        LootInstance instance = generateRandomBox();
        if (instance == null) return;
        addAwards(instance);
        p.getCollection().addLootBox(instance);
    }

    public static void addAwards(LootInstance instance) {
        LootTier tier = instance.getLootBox().getTier();
        List<LobbyCollectible> rewards = tier.generateRewards(instance.getLootBox());
        instance.getPossibleAwards().addAll(rewards);
    }

    public static void openLootBox(ExtronPlayer p, LootBox box) {
        LootInstance instance = new LootInstance(box);
        addAwards(instance);
        instance.open(p);
    }

    public static LootInstance generateRandomBox() {
        LootBox box = ListUtils.weightedRandomItem(lootBoxes,LootBox::getChance);
        if (box == null) return null;
        return new LootInstance(box);
    }

    public static JsonContainer saveAward(LobbyCollectible lc) {
        JsonContainer c = new JsonContainer();
        c.set("id",lc.getId());
        c.set("type",lc.getType().getId());
        return c;
    }

    public static List<LootBox> getLootBoxes() {
        return lootBoxes;
    }

    public static LootInstance createCustomBox(ExtronPlayer player, LobbyCollectible[] awards) {
        LootInstance instance = new LootInstance(customLootBox);
        for (LobbyCollectible c : awards) {
            instance.getPossibleAwards().add(c);
        }
        player.getCollection().addLootBox(instance);
        return instance;
    }
}
