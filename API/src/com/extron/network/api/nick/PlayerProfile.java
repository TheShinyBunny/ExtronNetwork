package com.extron.network.api.nick;

import java.util.UUID;

public class PlayerProfile {

    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\",\"name\":\"" + name + "\"}";
    }
}
