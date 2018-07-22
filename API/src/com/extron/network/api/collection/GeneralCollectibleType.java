package com.extron.network.api.collection;

import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GeneralCollectibleType extends CollectibleTypeBase {

    public static final GeneralCollectibleType LOOT_BOX = (GeneralCollectibleType) new GeneralCollectibleType("lootbox")
            .setName("LootBox")
            .setDescription("Loot boxes with random awesome prizes!")
            .setIcon(Material.CHEST);
    public static final GeneralCollectibleType KIT = (GeneralCollectibleType) new GeneralCollectibleType("kit")
            .setName("Kit")
            .setDescription("Various kits to find or buy in games!")
            .setIcon(Material.IRON_PICKAXE);

    public GeneralCollectibleType(String id) {
        super(id);
    }

    @Override
    public void select(ExtronPlayer player, Selectable c) {

    }

    @Override
    public void deselect(ExtronPlayer player) {

    }

    @Override
    public void menuClick(Collectible c, ExtronPlayer player, ButtonClickEvent click) {
        if (menuClick != null) {
            menuClick.test(c,player,click);
        }
    }

    @Override
    public List<? extends CategoryItem> getAll() {
        return new ArrayList<>();
    }

    @Override
    public String getParentName() {
        return "";
    }

    @Override
    public boolean isMainCategory() {
        return true;
    }

}
