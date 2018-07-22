package com.extron.network.islands.buildings;

import org.bukkit.Location;
import org.bukkit.Material;

public class BuildingBlacksmith implements Building {
    @Override
    public boolean isBasic() {
        return false;
    }

    @Override
    public boolean isImportant() {
        return true;
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
        return "blacksmith";
    }

    @Override
    public String getDisplayName() {
        return "Blacksmith";
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_CHESTPLATE;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "The blacksmith is one of the 2 important buildings, which without them you won't be able to continue constructing or upgrading other buildings. Here you can buy weapons and armor to help you fight!";
    }
}
