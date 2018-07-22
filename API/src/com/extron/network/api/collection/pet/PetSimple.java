package com.extron.network.api.collection.pet;

import com.extron.network.api.collection.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class PetSimple implements Pet {

    protected EntityType type;
    protected String name;
    private final Rarity rarity;
    protected String id;
    protected Material icon;
    protected final int iconDamage;

    public PetSimple(EntityType type, Rarity rarity, String name, Material icon, int iconDamage) {
        this.type = type;
        this.name = name;
        this.rarity = rarity;
        this.id = type.name().toLowerCase();
        this.icon = icon;
        this.iconDamage = iconDamage;
    }

    @Override
    public EntityType getEntityType() {
        return type;
    }

    @Override
    public String getDefaultName() {
        return name;
    }

    @Override
    public void onPetSpawned(Entity entity) {

    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public boolean foundInBasicLoot() {
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return getDefaultName();
    }

    @Override
    public Material getIcon() {
        return icon;
    }

    @Override
    public int getIconDamage() {
        return iconDamage;
    }


}
