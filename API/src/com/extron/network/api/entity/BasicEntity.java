package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BasicEntity extends ExtronEntity {

    protected Location spawnLocation;

    public BasicEntity(ExtronWorld world, Entity entity) {
        super(world, entity);
    }

    public BasicEntity(ExtronWorld world) {
        super(world);
    }

    @Override
    public void spawn() {
        if (this.world != null && getType() != EntityType.UNKNOWN) {
            if (entity != null) {
                kill();
            }
            spawnLocation = spawnLocation == null ? world.getSpawnPoint() : spawnLocation;
            entity = (CraftEntity) world.spawnEntity(spawnLocation,getType());
            world.getEntities().add(this);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void kill() {
        if (entity != null) {
            entity.remove();
            entity = null;
            if (world != null) {
                world.getEntities().remove(this);
            }
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
