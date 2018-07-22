package com.extron.network.islands.buildings;

import org.bukkit.Location;
import org.bukkit.Material;

public class BuildingCarpentry implements Building {
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
        return "carpentry";
    }

    @Override
    public String getDisplayName() {
        return "The Carpenter";
    }

    @Override
    public Material getIcon() {
        return Material.WOOD;
    }

    @Override
    public int getIconDamage() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "At the Carpenter you can gather wood to deposit later in the Bank.";
    }
}
