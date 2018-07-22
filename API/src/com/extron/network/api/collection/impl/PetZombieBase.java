package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import java.util.function.Consumer;


public abstract class PetZombieBase extends PetGroup<Zombie> {

    public PetZombieBase(String name, Material icon, int iconDamage) {
        super(name,icon,iconDamage);
    }

    @Override
    public void addItems() {
        addSubPet(Rarity.COMMON,getName(),Material.ROTTEN_FLESH,0,new ZombieModifier(false,false));
        addSubPet(Rarity.RARE,"Baby " + getName(),Material.POISONOUS_POTATO,0,new ZombieModifier(true,false));
    }

    public abstract String getName();

    public PetZombieBase(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Zombie> modifier) {
        super(rarity, name, icon, iconDamage, modifier);
    }

    private static class ZombieModifier implements Consumer<Zombie> {

        private final boolean baby;
        private final boolean villager;

        public ZombieModifier(boolean baby, boolean villager) {
            this.baby = baby;
            this.villager = villager;
        }

        @Override
        public void accept(Zombie zombie) {
            zombie.setVillager(villager);
            zombie.setBaby(baby);
        }
    }

    public static class Regular extends PetZombieBase {

        public Regular() {
            super("Zombie", Material.ROTTEN_FLESH, 0);
        }

        @Override
        public void addItems() {
            super.addItems();
            addSubPet(Rarity.EPIC, "Zombie Villager", Material.EMERALD_BLOCK, 0,new ZombieModifier(false,true));
            addSubPet(Rarity.LEGENDARY, "Baby Zombie Villager", Material.EMERALD, 0,new ZombieModifier(true,true));
        }

        public Regular(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Zombie> modifier) {
            super(rarity, name, icon, iconDamage, modifier);
        }

        @Override
        public String getName() {
            return "Zombie";
        }

        @Override
        protected PetGroup<Zombie> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Zombie> modifier) {
            return new Regular(rarity,name,icon,iconDamage,modifier);
        }

        @Override
        public EntityType getEntityType() {
            return EntityType.ZOMBIE;
        }
    }

    public static class Pigman extends PetZombieBase {

        public Pigman() {
            super("Zombie Pigman", Material.GOLD_NUGGET, 0);
        }

        public Pigman(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Zombie> modifier) {
            super(rarity, name, icon, iconDamage, modifier);
        }

        @Override
        public String getName() {
            return "Zombie Pigman";
        }

        @Override
        protected PetGroup<Zombie> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Zombie> modifier) {
            return new Pigman(rarity,name,icon,iconDamage,modifier);
        }

        @Override
        public EntityType getEntityType() {
            return EntityType.PIG_ZOMBIE;
        }
    }

}
