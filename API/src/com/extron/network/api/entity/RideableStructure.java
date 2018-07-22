package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.building.Structure;
import org.bukkit.Location;

public abstract class RideableStructure extends EntityStructure implements Rideable {

    protected final ExtronPlayer rider;
    protected EntitySeatable seat;

    public RideableStructure(ExtronWorld world, Structure s, ExtronPlayer rider) {
        super(world, s);
        this.rider = rider;
    }

    @Override
    public void spawn() {
        super.spawn();
        this.seat = createSeat();
        this.seat.setSpawnLocation(getSeatLocation());
        this.seat.spawn();
        this.setPassenger(rider);

    }

    public abstract EntitySeatable createSeat();

    @Override
    public void kill() {
        super.kill();
        dismountPassenger();
        if (seat != null) {
            seat.kill();
        }
    }

    @Override
    public void setPassenger(ExtronEntity e) {
        seat.setPassenger(e);
    }

    @Override
    public void dismountPassenger() {
        seat.dismountPassenger();
    }

    @Override
    public ExtronEntity getPassenger() {
        return rider;
    }

    public abstract Location getSeatLocation();
}
