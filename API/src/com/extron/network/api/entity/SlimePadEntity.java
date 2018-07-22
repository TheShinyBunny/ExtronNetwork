package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;

public class SlimePadEntity extends BasicEntity {

    public SlimePadEntity(ExtronWorld world) {
        super(world);
    }

    @Override
    public void spawn() {
        super.spawn();
        if (entity != null) {
            ArmorStand as = (ArmorStand)entity;
            as.setGravity(false);
            as.setHelmet(new ItemStack(Material.SLIME_BLOCK));
            as.setVisible(false);
            as.setHeadPose(new EulerAngle(0,90.0,0));
        }
    }

    @Override
    public void tick() {
        List<ExtronPlayer> ps = world.getNearbyPlayers(this,1.65);
        for (ExtronPlayer p : ps) {
            p.handle.setVelocity(new Vector(Math.random() * 0.05,0.8,Math.random() * 0.05));
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }
}
