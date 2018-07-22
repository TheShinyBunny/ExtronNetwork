package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.building.Structure;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import net.minecraft.server.v1_8_R1.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftFallingSand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;

import java.util.HashMap;
import java.util.Map;

public class EntityRocketShip extends RideableStructure {

    private int time = 300;

    private final double speed = 0.2;

    public EntityRocketShip(ExtronWorld world, Structure s, ExtronPlayer rider) {
        super(world,s,rider);
    }

    @Override
    public EntitySeatable createSeat() {
        return new SeatableArmorStand(world);
    }

    @Override
    public Location getSeatLocation() {
        return spawnLocation.clone().add(1.5,7,1.5);
    }

    @Override
    public void tick() {
        if (rider == null || rider.handle == null || time < 0 || seat.entity == null) {
            this.kill();
            return;
        }
        time--;
        entity.teleport(entity.getLocation().add(0,speed,0));
        for (EntityBlock b : pieces) {
            Location loc = b.entity.getLocation().add(0,speed,0);
            b.entity.getHandle().setPosition(loc.getX(),loc.getY(),loc.getZ());
        }
        Location loc = seat.entity.getLocation().add(0,speed,0);
        seat.entity.getHandle().setPosition(loc.getX(),loc.getY(),loc.getZ());
        world.spawnParticle(EnumParticle.SMOKE_LARGE, entity.getLocation().add(1, 0.5, 1), 0.3, 0.3, 0.3, 0.05, 10, null);
    }
}
