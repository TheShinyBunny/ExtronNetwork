package com.extron.network.api.entity;

public interface Rideable {

    void setPassenger(ExtronEntity e);

    void dismountPassenger();

    ExtronEntity getPassenger();

}
