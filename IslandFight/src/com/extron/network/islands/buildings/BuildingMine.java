package com.extron.network.islands.buildings;

import org.bukkit.Location;
import org.bukkit.Material;

public class BuildingMine implements Building {
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
        return "mine";
    }

    @Override
    public String getDisplayName() {
        return "The Mine";
    }

    @Override
    public Material getIcon() {
        return Material.IRON_PICKAXE;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "The mine will provide you with stone, to deposit later in the Bank.";
    }
}
