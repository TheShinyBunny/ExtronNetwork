package com.extron.network.islands.buildings;

import org.bukkit.Location;
import org.bukkit.Material;

public class BaseBuilding implements Building {
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
        return "base";
    }

    @Override
    public String getDisplayName() {
        return "Base";
    }

    @Override
    public Material getIcon() {
        return Material.WORKBENCH;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "The core building of your island. The ground floor is where you spawn every time an attack is coming, and where your Ender Chests are. The basement is what you need to protect from the attackers.";
    }
}
