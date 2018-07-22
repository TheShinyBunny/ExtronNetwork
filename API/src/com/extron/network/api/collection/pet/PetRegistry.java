package com.extron.network.api.collection.pet;

import com.extron.network.api.Main;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.collection.impl.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.function.Supplier;

public enum PetRegistry {
    CREEPER(EntityType.CREEPER,Material.TNT,"Creeper",Rarity.COMMON),
    SKELETON(EntityType.SKELETON,Material.BOW,"Skeleton",Rarity.COMMON),
    SPIDER(EntityType.SPIDER,Material.SPIDER_EYE,"Spider",Rarity.COMMON),
    CAVE_SPIDER(EntityType.CAVE_SPIDER,Material.FERMENTED_SPIDER_EYE,"Cave Spider",Rarity.RARE),
    WITCH(EntityType.WITCH,Material.POTION,"Witch",Rarity.RARE),
    ENDERMAN(EntityType.ENDERMAN,Material.EYE_OF_ENDER,"Enderman",Rarity.RARE),
    ENDERMITE(EntityType.ENDERMITE,Material.ENDER_PEARL,"Endermite",Rarity.EPIC),
    SILVERFISH(EntityType.SILVERFISH,Material.MONSTER_EGGS,3,"Silverfish",Rarity.EPIC),
    IRON_GOLEM(EntityType.IRON_GOLEM,Material.IRON_BLOCK,"Iron Golem",Rarity.LEGENDARY),
    SNOW_GOLEM(EntityType.SNOWMAN,Material.SNOW_BLOCK,"Snowman",Rarity.EPIC),
    BLAZE(EntityType.BLAZE,Material.BLAZE_ROD,"Blaze",Rarity.RARE),

    PIG(()->new PetAgeable<>(EntityType.PIG,"Pig",Material.PORK,0,Rarity.COMMON,Rarity.RARE)),
    COW(()->new PetAgeable<>(EntityType.COW,"Cow",Material.LEATHER,0,Rarity.COMMON,Rarity.RARE)),
    CHICKEN(()->new PetAgeable<>(EntityType.CHICKEN,"Chicken",Material.RAW_CHICKEN,0,Rarity.RARE,Rarity.EPIC)),
    WOLF(()->new PetAgeable<>(EntityType.WOLF,"Wolf",Material.BONE,0,Rarity.RARE,Rarity.EPIC)),
    MOOSHROOM(()->new PetAgeable<>(EntityType.MUSHROOM_COW,"Mooshroom",Material.RED_MUSHROOM,0,Rarity.EPIC,Rarity.LEGENDARY)),
    VILLAGER(()->new PetAgeable<>(EntityType.VILLAGER,"Villager",Material.EMERALD,0,Rarity.RARE,Rarity.RARE)),

    SLIME(SlimePetBase.RegularSlime::new),
    MAGMA_CUBE(SlimePetBase.MagmaCube::new),
    ZOMBIE(PetZombieBase.Regular::new),
    ZOMBIE_PIGMAN(PetZombieBase.Pigman::new),
    HORSE(PetHorse::new),
    SHEEP(PetSheep::new),
    OCELOT(PetOcelot::new),
    RABBIT(PetRabbit::new);

    private Material icon;
    private String name;
    private Rarity rarity;
    private int damage;
    private EntityType type;
    private Supplier<Pet> instance;

    PetRegistry(Supplier<Pet> instance) {
        this.instance = instance;
    }

    PetRegistry(EntityType type, Material icon, int iconDamage, String name, Rarity rarity) {
        this.type = type;
        this.icon = icon;
        this.damage = iconDamage;
        this.name = name;
        this.rarity = rarity;
    }

    PetRegistry(EntityType type, Material icon, String name, Rarity rarity) {
        this(type,icon,0,name,rarity);
    }


    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void registerPet() {
        Pet pet;
        if (instance == null) {
            pet = new PetSimple(type,rarity,name,icon,damage);
        } else {
            pet = instance.get();
        }
        Main.registerCollectible(pet);
    }
}
