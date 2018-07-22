package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class ExtronFallingBlock extends BasicEntity {

    private final Location location;
    private final Material type;
    private final byte data;
    private final boolean placeOnLand;
    private final Vector velocity;

    public ExtronFallingBlock(ExtronWorld world, Location loc, Material type, byte data, boolean placeOnLand, Vector vel) {
        super(world);
        this.world = world;
        this.location = loc;
        this.type = type;
        this.data = data;
        this.placeOnLand = placeOnLand;
        this.velocity = vel;
    }

    @Override
    public void spawn() {
        this.entity = (CraftEntity) world.handle.spawnFallingBlock(location,type,data);
        this.world.getEntities().add(this);
        this.entity.setVelocity(velocity);
    }

    public boolean onLand() {
        this.world.getEntities().remove(this);
        this.entity = null;
        return placeOnLand;
    }

    @Override
    public void kill() {

    }

    @Override
    public EntityType getType() {
        return EntityType.FALLING_BLOCK;
    }

    public void setNeverDespawn() {
        if (this.entity != null) {
            entity.getHandle().ticksLived = Integer.MIN_VALUE;
        }
    }
}
