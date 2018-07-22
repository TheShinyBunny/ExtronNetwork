package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class SeatableArmorStand extends EntitySeatable {

    public SeatableArmorStand(ExtronWorld world) {
        super(world);
    }

    @Override
    public void spawn() {
        super.spawn();
        if (entity != null) {
            ArmorStand as = (ArmorStand)entity;
            as.setSmall(true);
            as.setVisible(false);
            as.setGravity(false);
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }
}
