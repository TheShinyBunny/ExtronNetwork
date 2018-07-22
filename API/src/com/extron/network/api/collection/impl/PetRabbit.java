package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.pet.PetGroup;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;

import java.util.function.Consumer;

public class PetRabbit extends PetGroup<Rabbit> {
    public PetRabbit(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Rabbit> modifier) {
        super(rarity, name, icon, iconDamage, modifier);
        this.id = "rabbit_" + ((RabbitTypeModifier)modifier).type.name().toLowerCase().replaceAll("and_","");
    }

    @Override
    public void addItems() {
        addSubPet(Rarity.RARE,"Rabbit (White)",Material.WOOL,0,new RabbitTypeModifier(Rabbit.Type.WHITE));
        addSubPet(Rarity.RARE,"Rabbit (Black)",Material.WOOL,15,new RabbitTypeModifier(Rabbit.Type.BLACK));
        addSubPet(Rarity.RARE,"Rabbit (Salt and Pepper)",Material.WOOL,8,new RabbitTypeModifier(Rabbit.Type.SALT_AND_PEPPER));
        addSubPet(Rarity.RARE,"Rabbit (Black and White)",Material.WOOL,7,new RabbitTypeModifier(Rabbit.Type.BLACK_AND_WHITE));
        addSubPet(Rarity.RARE,"Rabbit (Brown)",Material.WOOL,12,new RabbitTypeModifier(Rabbit.Type.BROWN));
        addSubPet(Rarity.RARE,"Rabbit (Gold)",Material.WOOL,1,new RabbitTypeModifier(Rabbit.Type.GOLD));
        addSubPet(Rarity.LEGENDARY,"Killer Bunny",Material.GOLD_SWORD,0,new RabbitTypeModifier(Rabbit.Type.THE_KILLER_BUNNY));
    }

    public PetRabbit() {
        super("Rabbit",Material.RABBIT_FOOT,0);
    }

    @Override
    protected PetGroup<Rabbit> create(Rarity rarity, String name, Material icon, int iconDamage, Consumer<Rabbit> modifier) {
        return new PetRabbit(rarity,name,icon,iconDamage,modifier);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.RABBIT;
    }

    private static class RabbitTypeModifier implements Consumer<Rabbit> {

        private Rabbit.Type type;

        public RabbitTypeModifier(Rabbit.Type type) {
            this.type = type;
        }

        @Override
        public void accept(Rabbit rabbit) {
            rabbit.setRabbitType(type);
        }
    }
}
