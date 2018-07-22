package com.extron.network.api.entity;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;

public class EntitySeatable extends BasicEntity implements Rideable {

    public EntitySeatable(ExtronWorld world) {
        super(world);
    }

    @Override
    public void setPassenger(ExtronEntity e) {
        if (entity != null) {
            this.entity.setPassenger(e.getEntity());
            this.passenger = e;
            e.riding = this;
        }
    }

    @Override
    public void dismountPassenger() {
        if (entity != null) {
            if (entity.getPassenger() != null) {
                this.entity.eject();
                if (passenger != null) {
                    passenger.riding = null;
                }
                this.passenger = null;
            }
        }
    }

    @Override
    public void kill() {
        this.dismountPassenger();
        super.kill();
    }
}
