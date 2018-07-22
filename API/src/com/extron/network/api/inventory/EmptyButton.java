package com.extron.network.api.inventory;

import org.bukkit.Material;

public class EmptyButton extends ButtonBase {

    private final Material type;

    public EmptyButton(int slot, Material type) {
        super(slot);
        this.type = type;
    }

    public EmptyButton(Material type) {
        this.type = type;
    }

    @Override
    public Material getType() {
        return type;
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
        return "";
    }

    @Override
    public ItemLore getLore() {
        return null;
    }

    @Override
    public ClickAction getAction() {
        return null;
    }
}
