package com.extron.network.api.collection.pet;

import com.extron.network.api.Main;
import com.extron.network.api.collection.*;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class PetGroup<T extends Entity> extends PetSimple implements Category {

    private final boolean superType;
    private Consumer<T> modifier;
    protected List<PetGroup<T>> subItems = new ArrayList<>();
    public PetGroup<T> mainGroup;

    public PetGroup(Rarity rarity, String name, Material icon, int iconDamage, Consumer<T> modifier) {
        super(EntityType.UNKNOWN, rarity, name, icon, iconDamage);
        this.type = getEntityType();
        this.id = name.toLowerCase().replaceAll("\\s","_");
        this.modifier = modifier;
        superType = false;
    }

    public abstract void addItems();

    public PetGroup(String name, Material icon, int iconDamage) {
        super(EntityType.UNKNOWN, Rarity.COMMON, name, icon, iconDamage);
        this.type = getEntityType();
        this.id = name.toLowerCase().replaceAll("\\s","_") + "_group";
        this.superType = true;
        addItems();
        for (PetGroup<T> i : subItems) {
            i.mainGroup = this;
        }
    }

    @Override
    public void onPetSpawned(Entity entity) {
        if (modifier != null && !superType) {
            modifier.accept((T) entity);
        }
    }

    public void addSubPet(Rarity rarity, String name, Material icon, int iconDamage, Consumer<T> modifier) {
        PetGroup<T> item = create(rarity,name,icon,iconDamage,modifier);
        this.subItems.add(item);
        Main.registerCollectible(item);
    }

    protected abstract PetGroup<T> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<T> modifier);

    @Override
    public abstract EntityType getEntityType();

    @Override
    public Category getCategory() {
        if (superType) return this;
        if (mainGroup == null) {
            System.out.println("null main group!");
        }
        return mainGroup;
    }

    @Override
    public List<PetGroup<T>> getAll() {
        return subItems;
    }

    @Override
    public String getParentName() {
        return "Pet";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isMainCategory() {
        return superType;
    }

    @Override
    public boolean isObtainable() {
        return !superType;
    }
}
