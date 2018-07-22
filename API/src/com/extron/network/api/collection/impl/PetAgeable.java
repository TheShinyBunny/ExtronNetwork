package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

import java.util.function.Consumer;

public class PetAgeable<T extends Ageable> extends PetGroup<T> {
    private Rarity adultRarity;
    private Rarity babyRarity;
    private EntityType type2;

    public PetAgeable(EntityType type, String name, Material icon, int iconDamage, Rarity adult, Rarity baby) {
        super(name, icon, iconDamage);
        this.type2 = type;
        this.adultRarity = adult;
        this.babyRarity = baby;
        addSubPet(adultRarity,name,icon,iconDamage,new BabyModifier(false));
        addSubPet(babyRarity,"Baby " + name,icon,iconDamage,new BabyModifier(true));
        for (PetGroup<T> i : subItems) {
            i.mainGroup = this;
        }
    }

    public PetAgeable(EntityType type, Rarity rarity, String name, Material icon, int iconDamage, Consumer<T> modifier) {
        super(rarity, name, icon, iconDamage, modifier);
        this.type = type;
    }

    @Override
    public void addItems() {

    }

    @Override
    protected PetGroup<T> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<T> modifier) {
        return new PetAgeable<>(getEntityType(),rarity,name,icon,iconDamage,modifier);
    }

    @Override
    public EntityType getEntityType() {
        return type2;
    }

    protected class BabyModifier implements Consumer<T> {
        private final boolean baby;

        public BabyModifier(boolean baby) {
            this.baby = baby;
        }

        @Override
        public void accept(T t) {
            if (baby) {
                t.setAge(-200);
                t.setAgeLock(true);
            } else {
                t.setAge(0);
            }
        }
    }
}
