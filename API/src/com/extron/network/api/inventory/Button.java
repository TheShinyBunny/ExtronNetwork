package com.extron.network.api.inventory;

import com.extron.network.api.inventory.base.ExtronStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface Button extends MenuContent {
    Material getType();

    int getCount();

    int getDamage();

    String getDisplayName();

    ItemLore getLore();

    ClickAction getAction();

    InteractAction getInteractAction();

    @Override
    Button addTo(InventoryMenu menu);

    ExtronStack createItem();
}
