package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

import java.util.function.Consumer;

public class PetHorse extends PetGroup<Horse> {

    public PetHorse() {
        super("Horse", Material.SADDLE, 0);
    }

    public PetHorse(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Horse> modifier) {
        super(rarity, name, icon, iconDamage, modifier);
    }

    @Override
    public void addItems() {
        addSubPet(Rarity.COMMON,"Horse",Material.SADDLE,0,h->h.setVariant(Horse.Variant.HORSE));
        addSubPet(Rarity.COMMON,"Donkey",Material.CHEST,0,h->h.setVariant(Horse.Variant.DONKEY));
        addSubPet(Rarity.RARE,"Mule",Material.ENDER_CHEST,0,h->h.setVariant(Horse.Variant.MULE));
    }

    @Override
    protected PetGroup<Horse> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Horse> modifier) {
        return new PetHorse(rarity,name,icon,iconDamage,modifier);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.HORSE;
    }
}
