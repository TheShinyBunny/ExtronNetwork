package com.extron.network.api.collection;

import com.extron.network.api.Main;
import com.extron.network.api.collection.loot.LootBoxManager;
import com.extron.network.api.collection.loot.LootInstance;
import com.extron.network.api.collection.pet.Pet;
import com.extron.network.api.collection.pet.PetInstance;
import com.extron.network.api.collection.pet.PlayerPet;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerData;
import com.extron.network.api.scoreboard.ScoreLines;
import com.extron.network.api.utils.JsonContainer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.Loadable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCollection implements Loadable<PlayerData> {

    private final ExtronPlayer player;
    private List<Collectible> collectibles = new ArrayList<>();
    private Map<CollectibleType,Selectable> selected = new HashMap<>();
    private Map<Pet,PetInstance> pets = new HashMap<>();
    private List<LootInstance> lootBoxes = new ArrayList<>();

    public PlayerCollection(ExtronPlayer player) {
        this.player = player;
    }

    public boolean contains(Collectible c) {
        return collectibles.contains(c);
    }

    public Selectable getSelected(CollectibleType type) {
        return selected.get(type);
    }

    public void select(CollectibleType type, Selectable c) {
        if (getSelected(type) == c) return;
        this.selected.put(type,c);
        this.player.setData("collectibles.selected." + type.getId(),c.getId());
    }

    public void add(Collectible c) {
        if (this.contains(c)) return;
        if (!c.isObtainable()) {
            return;
        }
        this.collectibles.add(c);
        List<String> list = this.player.getData().getStringList("collectibles." + c.getType().getId());
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(c.getId());
        this.player.setData("collectibles." + c.getType().getId(),list);
    }

    public void remove(Collectible c) {
        if (!this.contains(c)) return;
        this.collectibles.remove(c);
        List<String> list = this.player.getData().getStringList("collectibles." + c.getType().getId());
        if (list == null) {
            return;
        }
        list.remove(c.getId());
        this.player.setData("collectibles." + c.getType().getId(),list);
    }

    @Override
    public void load(PlayerData obj) {
        if (obj.getJsonObject("collectibles") != null) {
            JsonContainer jc = obj.getJsonObject("collectibles");
            for (CollectibleType type : Main.getCollectibleTypes()) {
                List<String> list = jc.getStringList(type.getId());
                if (list != null) {
                    for (String s : list) {
                        Collectible c = Main.getCollectible(s,type);
                        if (c != null) {
                            this.collectibles.add(c);
                        }
                    }
                }
            }
        }
        if (obj.getJsonObject("collectibles.pet_data") != null) {
            JsonContainer jc = obj.getJsonObject("collectibles.pet_data");
            for (Collectible pet : Main.getCollectiblesOfType(LobbyCollectibleType.PET)) {
                if (this.contains(pet) && pet instanceof Pet) {
                    JsonContainer petdata = jc.getJsonObject(pet.getId());
                    if (petdata != null) {
                        this.pets.put((Pet) pet,new PlayerPet(player,(Pet)pet,petdata));
                    }
                }
            }
        }
        if (obj.getJsonObject("collectibles.selected") != null) {
            JsonContainer jc = obj.getJsonObject("collectibles.selected");
            for (CollectibleType type : Main.getCollectibleTypes()) {
                String id = jc.getString(type.getId());
                if (id != null) {
                    Collectible c = Main.getCollectible(id,type);
                    if (c instanceof Selectable) {
                        this.selected.put(type, (Selectable) c);
                        if (type instanceof CollectibleTypeBase) {
                            if (((CollectibleTypeBase) type).getOnSelect() != null) {
                                ((CollectibleTypeBase) type).getOnSelect().accept(player, (Selectable) c);
                            }
                        }
                    }
                }
            }
        }
        if (obj.getObjectList("collectibles.loot_boxes") != null) {
            List<JsonContainer> boxes = obj.getObjectList("collectibles.loot_boxes");
            for (JsonContainer c : boxes) {
                this.lootBoxes.add(LootBoxManager.createFromJson(c));
            }
        }
    }

    public PetInstance getPetInstance(Pet pet) {
        PetInstance instance = pets.get(pet);
        if (instance == null) {
            instance = new PlayerPet(player,pet);
            this.addPet(pet,instance);
        }
        return instance;
    }

    public void addPet(Pet pet, PetInstance instance) {
        this.pets.put(pet,instance);
        this.player.setData("collectibles.pet_data." + pet.getId(),instance.saveToJson());
    }

    public void deselect(CollectibleType type) {
        this.selected.remove(type);
        this.player.setData("collectibles.selected." + type.getId(),null);
    }

    public List<Collectible> getAll(CollectibleType type) {
        return ListUtils.filter(collectibles,c->c.getType()==type);
    }

    public void addLootBox(LootInstance instance) {
        this.lootBoxes.add(instance);
        List<JsonContainer> list = this.player.getData().getObjectList("collectibles.loot_boxes");
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(instance.toJson());
        this.player.setData("collectibles.loot_boxes",list);
    }

    public List<Collectible> getFoundOfCategory(Category category) {
        return ListUtils.filter(collectibles,c->c instanceof LobbyCollectible && ((LobbyCollectible) c).getCategory()==category);
    }

    public void removeLootBox(LootInstance box) {
        this.lootBoxes.remove(box);
        this.player.setData("collectibles.loot_boxes",ListUtils.convertAll(lootBoxes,LootInstance::toJson));
    }
}
