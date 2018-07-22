package com.extron.network.api.collection.pet;

import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.PathfinderGoal;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

public class PathfinderGoalMoveTowardsLook extends PathfinderGoal {
    private final float speed;
    private final PetInstance pet;
    private final EntityInsentient entity;

    public PathfinderGoalMoveTowardsLook(PetInstance pet, float speed) {
        this.speed = speed;
        this.pet = pet;
        this.entity = (EntityInsentient) pet.getEntity().getHandle();
    }


    @Override
    public boolean a() {
        return pet.isBeingRidden() && pet.getOwner() != null && pet.getOwner().isOnline();
    }

    @Override
    public void d() {
        this.entity.getNavigation().n();
    }

    @Override
    public void e() {
        Block b = pet.getOwner().handle.getTargetBlock((Set<Material>) null,0);
        this.entity.getNavigation().a(b.getX(),b.getY(),b.getZ(),speed);
    }
}
