package com.extron.network.api.collection.pet;

import com.extron.network.api.Main;
import com.extron.network.api.players.ExtronPlayer;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.GameMode;

public class PathfinderGoalFollowPetOwner extends PathfinderGoal {

    private final EntityInsentient pet;
    private final double followSpeed;
    private final NavigationAbstract petPathfinder;
    private final PetInstance petInstance;
    private int timeToRecalcPath;
    float maxDist;
    float minDist;
    private ExtronPlayer theOwner;

    public PathfinderGoalFollowPetOwner(PetInstance thePetIn, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        this.pet = (EntityInsentient) thePetIn.getEntity().getHandle();
        this.petInstance = thePetIn;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = thePetIn.getPathNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.a(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean a()
    {
        ExtronPlayer p = this.petInstance.getOwner();

        if (p == null)
        {
            return false;
        }
        else if (p.handle == null || p.handle.getGameMode() == GameMode.SPECTATOR)
        {
            return false;
        }
        else if (this.petInstance.isBeingRidden())
        {
            return false;
        }
        else if (this.pet.h(p.getNMS()) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.theOwner = p;
            return true;
        }
    }

    /**
     * Returns whether an in-progress PathfinderGoal should continue executing
     */
    @Override
    public boolean b()
    {
        return !this.petPathfinder.m() && this.pet.h(this.theOwner.getNMS()) > (double)(this.maxDist * this.maxDist) && !this.petInstance.isBeingRidden();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void c()
    {
        this.timeToRecalcPath = 0;
    }

    /**
     * Resets the task
     */
    @Override
    public void d()
    {
        this.theOwner = null;
        this.petPathfinder.n();
    }

    /**
     * Updates the task
     */
    @Override
    public void e()
    {
        pet.getControllerLook().a(this.theOwner.getNMS(), 10.0F, (float)this.pet.bP());

        if (!this.petInstance.isBeingRidden())
        {
            if (--this.timeToRecalcPath <= 0)
            {
                this.timeToRecalcPath = 10;

                this.petPathfinder.a(this.theOwner.getNMS(), this.followSpeed);
            }
        }
    }
}
