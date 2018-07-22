package com.extron.network.api.entity;

import com.extron.network.api.players.ExtronPlayer;
import net.minecraft.server.v1_8_R1.EntityEnderPearl;
import org.bukkit.entity.EntityType;

public class SeatablePearl extends EntitySeatable {

    private final ExtronPlayer thrower;

    public SeatablePearl(ExtronPlayer thrower) {
        super(thrower.getWorld());
        this.thrower = thrower;
    }

    @Override
    public void spawn() {
        EntityEnderPearl pearl = new EntityEnderPearl(world.getNMS(),thrower.getNMS());
        world.handle.getHandle().addEntity(pearl);
        this.entity = pearl.getBukkitEntity();
        this.setPassenger(thrower);
    }

    @Override
    public void kill() {
        System.out.println("killed pearl");
        super.kill();
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDER_PEARL;
    }
}
