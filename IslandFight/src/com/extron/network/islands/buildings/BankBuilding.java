package com.extron.network.islands.buildings;

import org.bukkit.Location;
import org.bukkit.Material;

public class BankBuilding implements Building {
    @Override
    public boolean isBasic() {
        return true;
    }

    @Override
    public boolean isImportant() {
        return false;
    }

    @Override
    public void build(Location loc) {

    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public String getId() {
        return "bank";
    }

    @Override
    public String getDisplayName() {
        return "Bank";
    }

    @Override
    public Material getIcon() {
        return Material.EMERALD_BLOCK;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "In the bank you can deposit your stone and wood to get gold.";
    }
}
