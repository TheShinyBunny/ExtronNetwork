package com.extron.network.api.inventory;

import org.bukkit.Material;

public class EmptyErrorButton extends ButtonBase {
    private final String msg;
    private final String name;

    public EmptyErrorButton(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }

    @Override
    public Material getType() {
        return Material.THIN_GLASS;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getDamage() {
        return 14;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public ItemLore getLore() {
        return ItemLore.create().empty().description(msg);
    }

    @Override
    public ClickAction getAction() {
        return null;
    }
}
