package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public abstract class ExtronEntity {

    public boolean dead;
    protected ExtronWorld world;
    protected CraftEntity entity;
    protected String name;
    public ExtronEntity passenger;
    public ExtronEntity riding;
    public boolean invulnerable;

    public ExtronEntity(ExtronWorld world, Entity entity) {
        this.world = world;
        this.entity = (CraftEntity) entity;
        this.name = entity.getName();
    }

    public ExtronEntity(ExtronWorld world) {
        this.world = world;
    }

    public abstract void spawn();

    public ExtronEntity() {

    }

    public String getName() {
        return name;
    }

    public CraftEntity getEntity() {
        return entity;
    }

    public ExtronWorld getWorld() {
        return world;
    }

    public void setWorld(ExtronWorld world) {
        this.world = world;
    }

    public abstract void tick();

    public abstract void kill();

    public abstract EntityType getType();

    public ExtronEntity getPassenger() {
        return passenger;
    }

    public ExtronEntity getRidingEntity() {
        return riding;
    }

    protected double distanceTo(Location location) {
        return getEntity() == null ? 0 : location.distance(getEntity().getLocation());
    }
}
