package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

import java.util.function.Consumer;

public abstract class SlimePetBase extends PetGroup<Slime> {

    public SlimePetBase(String name, Material icon) {
        super(name, icon, 0);
    }

    @Override
    public void addItems() {
        addSubPet(Rarity.RARE,getName() + " (Small)",getIcon(),getIconDamage(),new SizeModifier(0));
        addSubPet(Rarity.COMMON,getName() + " (Medium)",getIcon(),getIconDamage(),new SizeModifier(1));
        addSubPet(Rarity.RARE,getName() + " (Big)",getIcon(),getIconDamage(),new SizeModifier(2));
        addSubPet(Rarity.EPIC,getName() + " (Huge)",getIcon(),getIconDamage(),new SizeModifier(3));
    }

    public SlimePetBase(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Slime> modifier) {
        super(rarity, name, icon, iconDamage, modifier);
        this.id = getName().toLowerCase().replaceAll("\\s","_") + "_" + ((SizeModifier)modifier).size;
    }

    public abstract String getName();

    private static class SizeModifier implements Consumer<Slime> {

        private int size;

        public SizeModifier(int size) {
            this.size = size;
        }

        @Override
        public void accept(Slime slime) {
            slime.setSize(size);
        }
    }

    public static class MagmaCube extends SlimePetBase {

        public MagmaCube(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Slime> modifier) {
            super(rarity, name, icon, iconDamage, modifier);
        }

        public MagmaCube() {
            super("Magma Cube",Material.MAGMA_CREAM);
        }

        @Override
        public String getName() {
            return "Magma Cube";
        }

        @Override
        protected PetGroup<Slime> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Slime> modifier) {
            return new MagmaCube(rarity,name,icon,iconDamage,modifier);
        }

        @Override
        public EntityType getEntityType() {
            return EntityType.MAGMA_CUBE;
        }
    }

    public static class RegularSlime extends SlimePetBase {

        public RegularSlime(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Slime> modifier) {
            super(rarity, name, icon, iconDamage, modifier);
        }

        public RegularSlime() {
            super("Slime",Material.SLIME_BALL);
        }

        @Override
        public String getName() {
            return "Slime";
        }

        @Override
        protected PetGroup<Slime> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Slime> modifier) {
            return new RegularSlime(rarity,name,icon,iconDamage,modifier);
        }

        @Override
        public EntityType getEntityType() {
            return EntityType.SLIME;
        }
    }
}
