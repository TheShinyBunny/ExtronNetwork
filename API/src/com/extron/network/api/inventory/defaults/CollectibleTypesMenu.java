package com.extron.network.api.inventory.defaults;

import com.extron.network.api.collection.LobbyCollectibleType;
import com.extron.network.api.inventory.Button;
import com.extron.network.api.inventory.ClickAction;
import com.extron.network.api.inventory.ItemLore;
import com.extron.network.api.inventory.SimpleButton;
import com.extron.network.api.utils.TextUtils;

import java.util.Collection;
import java.util.List;

public class CollectibleTypesMenu extends CategoriesMenuBase<LobbyCollectibleType> {
    @Override
    public List<LobbyCollectibleType> getCategories() {
        return LobbyCollectibleType.getAllTypes();
    }

    @Override
    public Button createButtonFor(LobbyCollectibleType category) {
        return new SimpleButton(category.getIcon())
                .setDamage(category.getIconDamage())
                .setDisplayName(TextUtils.pluralize(category.getName(),true))
                .setLore(ItemLore.description(category).empty().found(category.getFound(owner).size(),category.getAll().size()))
                .setAction(ClickAction.open(()->new LobbyTypeMenu<>(category,CollectibleTypesMenu::new)));
    }

    @Override
    public String getTitle() {
        return "Collectibles";
    }

    @Override
    public void onClose() {

    }
}
