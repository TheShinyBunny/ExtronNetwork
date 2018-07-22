package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import java.util.function.Consumer;

public class PetSheep extends PetAgeable<Sheep> {
    public PetSheep() {
        super(EntityType.SHEEP,"Sheep",Material.BARRIER,0,Rarity.COMMON,Rarity.RARE);
        this.icon = Material.WOOL;
    }

    @Override
    public void addItems() {
        for (DyeColor color : DyeColor.values()) {
            addSubPet(Rarity.COMMON, TextUtils.capitalize(color.name().toLowerCase()).replaceAll("_", " ") + " Sheep", Material.WOOL, color.getWoolData(), new SheepModifier(color,false));
            addSubPet(Rarity.RARE,"Baby " + TextUtils.capitalize(color.name().toLowerCase().replaceAll("_", " ")) + " Sheep",Material.WOOL,color.getWoolData(),new SheepModifier(color,true));
        }
    }

    @Override
    public void addSubPet(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Sheep> modifier) {
        if (icon != Material.BARRIER) {
            super.addSubPet(rarity, name, icon, iconDamage, modifier);
        }
    }

    public PetSheep(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Sheep> modifier) {
        super(EntityType.SHEEP, rarity, name, icon, iconDamage, modifier);
    }

    @Override
    protected PetGroup<Sheep> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Sheep> modifier) {
        return new PetSheep(rarity,name,icon,iconDamage,modifier);
    }

    private class SheepModifier extends BabyModifier {
        private final DyeColor color;

        public SheepModifier(DyeColor color, boolean baby) {
            super(baby);
            this.color = color;
        }

        @Override
        public void accept(Sheep sheep) {
            super.accept(sheep);
            sheep.setColor(color);
        }
    }
}
