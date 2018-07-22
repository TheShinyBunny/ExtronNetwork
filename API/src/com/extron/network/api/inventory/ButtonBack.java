package com.extron.network.api.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Supplier;

public class ButtonBack extends ButtonBase {
    private final ItemLore lore;
    private final Supplier<InventoryMenu> supply;

    public ButtonBack(int slot, Supplier<InventoryMenu> menuSupplier, ItemLore lore) {
        super(slot);
        this.supply = menuSupplier;
        this.lore = lore;
    }

    @Override
    public Material getType() {
        return Material.ARROW;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GRAY + "Back";
    }

    @Override
    public ItemLore getLore() {
        return lore;
    }

    @Override
    public ClickAction getAction() {
        return e -> e.getPlayer().openInventory(supply.get());
    }

}
