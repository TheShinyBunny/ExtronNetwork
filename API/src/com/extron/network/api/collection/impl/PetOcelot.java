package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

import java.util.function.Consumer;

public class PetOcelot extends PetGroup<Ocelot> {
    public PetOcelot(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Ocelot> modifier) {
        super(rarity, name, icon, iconDamage, modifier);
        this.id = ((OcelotTypeModifier)modifier).type.toString().toLowerCase();
    }

    @Override
    public void addItems() {
        addSubPet(Rarity.RARE,"Wild Ocelot",Material.RAW_FISH,0,new OcelotTypeModifier(Ocelot.Type.WILD_OCELOT));
        addSubPet(Rarity.EPIC,"Cat (Black)",Material.COOKED_FISH,0,new OcelotTypeModifier(Ocelot.Type.BLACK_CAT));
        addSubPet(Rarity.EPIC,"Cat (Red)",Material.COOKED_FISH,1,new OcelotTypeModifier(Ocelot.Type.RED_CAT));
        addSubPet(Rarity.EPIC,"Cat (Siamese)",Material.RAW_FISH,2,new OcelotTypeModifier(Ocelot.Type.SIAMESE_CAT));
    }

    public PetOcelot() {
        super("Ocelot", Material.RAW_FISH, 0);
    }

    @Override
    protected PetGroup<Ocelot> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Ocelot> modifier) {
        return new PetOcelot(rarity,name,icon,iconDamage,modifier);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.OCELOT;
    }

    private class OcelotTypeModifier implements Consumer<Ocelot> {
        private final Ocelot.Type type;

        public OcelotTypeModifier(Ocelot.Type type) {
            this.type = type;
        }

        @Override
        public void accept(Ocelot ocelot) {
            ocelot.setCatType(type);
        }
    }
}
