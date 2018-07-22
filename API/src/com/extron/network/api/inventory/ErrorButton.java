package com.extron.network.api.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ErrorButton extends ButtonBase {
    @Override
    public Material getType() {
        return Material.BARRIER;
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
        return ChatColor.RED + "ERROR";
    }

    @Override
    public ItemLore getLore() {
        return ItemLore.create()
                .description("An unknown error occurred while generating this button.");
    }

    @Override
    public ClickAction getAction() {
        return null;
    }
}
