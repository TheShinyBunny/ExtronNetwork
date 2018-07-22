package com.extron.network.api.hologram;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import net.minecraft.server.v1_8_R1.EntityArmorStand;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;

import java.util.*;


public abstract class PlayerHologram extends SavedHologram {

    private String id;
    private Map<ExtronPlayer,List<EntityArmorStand>> asps;
    private boolean init;

    public PlayerHologram(String id) {
        super(UUID.randomUUID());
        this.id = id;
        this.asps = new HashMap<>();
        if (Main.getMainConfig().getSection("player_holograms." + id) != null) {
            this.init = true;
            this.loc = Main.getMainConfig().getLocation("player_holograms." + id,null,false);
            this.world = Main.getWorld(loc.getWorld());
        } else {
            this.init = false;
        }
    }

    public void initialize(ExtronWorld world, Location loc) {
        this.world = world;
        this.loc = loc;
        this.init = true;
        Main.getMainConfig().setLocation("player_holograms." + id,loc,true,false);
        Main.getMainConfig().save();
    }

    public abstract List<String> getLines(ExtronPlayer p,List<String> lines);

    @Override
    public boolean spawn() {
        if (!init) return false;
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            this.spawnFor(p);
        }
        return true;
    }

    public void spawnFor(ExtronPlayer p) {
        if (!asps.containsKey(p)) {
            asps.put(p,new ArrayList<>());
        }
        Location current = loc.clone();
        for (String line : getLines(p,new ArrayList<>())) {
            EntityArmorStand as = new EntityArmorStand(world.getNMS(),current.getX(),current.getY(),current.getZ());
            as.setCustomName(line);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setInvisible(true);
            as.setSmall(true);
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(as);
            p.sendPacket(packet);
            this.asps.get(p).add(as);
            current.subtract(0,lineDistance,0);
        }
    }

    @Override
    public boolean despawn() {
        if (!init) return false;
        for (ExtronPlayer p : PlayerList.getOnlinePlayers()) {
            this.despawnFor(p);
        }
        return true;
    }

    public void despawnFor(ExtronPlayer p) {
        if (this.asps.containsKey(p)) {
            for (EntityArmorStand as : this.asps.get(p)) {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(as.getId());
                p.sendPacket(packet);
            }
            this.asps.remove(p);
        }
    }

    @Override
    public String toString() {
        return this.id;
    }

    public boolean isInitialized() {
        return init;
    }

    public String getId() {
        return id;
    }
}
