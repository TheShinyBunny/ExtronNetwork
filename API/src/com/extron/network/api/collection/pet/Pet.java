package com.extron.network.api.collection.pet;

import com.extron.network.api.collection.*;
import org.bukkit.entity.*;

public interface Pet extends LobbyCollectible {

    EntityType getEntityType();

    String getDefaultName();

    void onPetSpawned(Entity entity);

    @Override
    default LobbyCollectibleType getType() {
        return LobbyCollectibleType.PET;
    }

    @Override
    default String getDescription() {
        return "Spawns the " + getDefaultName().toLowerCase() + " pet!";
    }

    static void registerPets() {
        for (PetRegistry r : PetRegistry.values()) {
            r.registerPet();
        }
    }

}
