package com.extron.network.api.collection.pet;

import com.extron.network.api.Main;
import com.extron.network.api.entity.EntitySeatable;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.JsonContainer;
import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.NavigationAbstract;
import net.minecraft.server.v1_8_R1.PathfinderGoalMoveTowardsRestriction;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;

public class PlayerPet extends EntitySeatable implements PetInstance {

    private final ExtronPlayer owner;
    private Pet pet;
    private PetStats stats;
    private PetInstance.Status status;
    private String name;

    public PlayerPet(ExtronPlayer p, Pet collectible) {
        super(Main.getLobby());
        this.owner = p;
        this.pet = collectible;
        this.stats = new PetStats(new JsonContainer());
        this.status = Status.SAD;
        this.name = collectible.getDefaultName();
    }

    public PlayerPet(ExtronPlayer player, Pet pet, JsonContainer data) {
        super(Main.getLobby());
        this.owner = player;
        this.world = Main.getLobby();
        this.pet = pet;
        this.stats = new PetStats(data.getJsonObject("stats",new JsonContainer()));
        this.status = Status.valueOf(data.getString("status","SAD"));
        this.name = data.getString("name",pet.getDefaultName());
    }

    public ExtronPlayer getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void tick() {
        if (owner == null || !owner.isOnline() || !owner.getWorld().isLobby() || entity == null || entity.isDead()) {
            kill();
        }
    }

    @Override
    public void kill() {
        if (entity != null) {
            entity.remove();
            entity = null;
            dead = true;
        }
    }

    @Override
    public EntityType getType() {
        return pet.getEntityType();
    }

    @Override
    public PetStats getStats() {
        return stats;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void spawn() {
        invulnerable = true;
        EntityType type = pet.getEntityType();
        entity = (CraftEntity) owner.getWorld().handle.spawnEntity(owner.getLocation(),type);
        pet.onPetSpawned(entity);
        world.getEntities().add(this);
        CraftLivingEntity e = (CraftLivingEntity) this.entity;
        EntityInsentient living = (EntityInsentient) e.getHandle();
        living.targetSelector.a(0,new PathfinderGoalFollowPetOwner(this,1.0f,10.0f, 2.0f));
        living.goalSelector.a(1,new PathfinderGoalMoveTowardsLook(this,1.0f));
    }

    @Override
    public JsonContainer saveToJson() {
        JsonContainer json = new JsonContainer();
        json.set("stats",stats.saveToJson());
        json.set("status",status.toString());
        json.set("name",name);
        return json;
    }

    @Override
    public NavigationAbstract getPathNavigator() {
        return ((EntityInsentient)entity.getHandle()).getNavigation();
    }

    @Override
    public boolean isBeingRidden() {
        return this.getPassenger() != null;
    }

    @Override
    public void mountOwner() {
        this.setPassenger(owner);
    }

    @Override
    public void onPlayerDismount() {
        this.passenger = null;
        owner.riding = null;
    }

    public Pet getCollectible() {
        return pet;
    }
}
