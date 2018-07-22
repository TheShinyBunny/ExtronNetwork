package com.extron.network.api;

import com.extron.network.api.config.Config;
import com.extron.network.api.entity.ExtronEntity;
import com.extron.network.api.entity.SlimePadEntity;
import com.extron.network.api.game.MapCreator;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.hologram.SavedHologram;
import com.extron.network.api.parkour.Parkour;
import com.extron.network.api.parkour.ParkourMark;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.BlockPos;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.VoidGenerator;
import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtronWorld {

    public final CraftWorld handle;
    private final Config config;
    private List<SavedHologram> holograms;
    public GameManager currentGame;
    private List<ExtronPlayer> players;
    private List<Parkour> parkours;
    private List<ExtronEntity> entities;
    private MapCreator mapCreator;
    private boolean isMap;


    public ExtronWorld(CraftWorld handle, Config config) {
        this.handle = handle;
        this.holograms = new ArrayList<>();
        this.config = config;
        this.isMap = config.getString("gamemode") != null;
        players = new ArrayList<>();
        parkours = new ArrayList<>();
        entities = new ArrayList<>();
        this.loadHolograms();
        this.loadParkours();
    }

    private void loadParkours() {
        if (this.config.getSection("parkours") != null) {
            for (String id : this.config.getSection("parkours").getKeys(false)) {
                Parkour p = new Parkour(this);
                for (String n : this.config.getSection("parkours." + id).getKeys(false)) {
                    Location loc = this.config.getLocation("parkours." + id + "." + n + ".location",null,this,false);
                    SavedHologram h = this.getHologram(UUID.fromString(config.getString("parkours." + id + "." + n + ".hologram")));
                    if (h != null) {
                        p.loadCheckpoint(Integer.parseInt(n),loc,h);
                    }
                }
                this.parkours.add(p);
            }
        }
    }

    public SavedHologram getHologram(UUID uuid) {
        return ListUtils.firstMatch(holograms,h->h.getUUID().equals(uuid));
    }

    public static ExtronWorld createFromBukkit(World w) {
        return Main.createWorld(w.getName(),w.getEnvironment(),w.getWorldType(),w.getGenerator() instanceof VoidGenerator);
    }

    public String getName() {
        return handle.getName();
    }

    public WorldServer getNMS() {
        return handle.getHandle();
    }

    public List<SavedHologram> getHolograms() {
        return holograms;
    }

    public SavedHologram createHologram(String line, Location loc) {
        SavedHologram h = new SavedHologram(this,loc,UUID.randomUUID());
        h.addLine(line);
        h.spawn();
        this.holograms.add(h);
        return h;
    }

    public Entity spawnEntity(Location at, EntityType type) {
        return handle.spawnEntity(at,type);
    }

    public Config getConfig() {
        return config;
    }

    public File getFolder() {
        return handle.getWorldFolder();
    }

    public SavedHologram findNearestHologram(ExtronPlayer player) {
        SavedHologram closest = null;
        for (SavedHologram h : holograms) {
            if (closest == null || h.getLocation().distance(player.getLocation()) < closest.getLocation().distance(player.getLocation())) {
                closest = h;
            }
        }
        return closest;
    }

    public void tick() {
        List<ExtronEntity> remove = new ArrayList<>();
        for (ExtronEntity e : entities) {
            if (e.dead) {
                remove.add(e);
            } else {
                e.tick();
            }
        }
        remove.forEach(e->entities.remove(e));
    }

    private void loadHolograms() {
        if (config.getSection("holograms") != null) {
            for (String uuid : config.getSection("holograms").getKeys(false)) {
                System.out.println(uuid);
                SavedHologram h = new SavedHologram(this,config.getLocation("holograms." + uuid + ".location",null,this,false), UUID.fromString(uuid));
                h.addAllLines(config.getStringList("holograms." + uuid + ".lines"));
                h.spawn();
                holograms.add(h);
            }
        }
    }

    public void despawnHolograms() {
        holograms.forEach(SavedHologram::despawn);
    }

    public void broadcastMessage(ExtronPlayer p, String msg) {
        players.forEach(pl->pl.sendMessage(ChatColor.RED + ">>" + p.getDisplayName() + ChatColor.RESET + ": " + msg));
    }

    public void broadcastMessage(String msg) {
        players.forEach(p->p.sendMessage(msg));
    }

    public Location getSpawnPoint() {
        return handle.getSpawnLocation().add(0.5,0.5,0.5);
    }

    public List<ExtronPlayer> getPlayers() {
        return players;
    }

    public boolean isLobby() {
        return this.equals(Main.getLobby());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ExtronWorld && ((ExtronWorld) obj).handle.equals(handle);
    }

    public List<Parkour> getParkours() {
        return parkours;
    }

    public void onStepPressurePlate(ExtronPlayer p, Location location) {
        ParkourMark mark = this.getParkourLandmark(location);
        if (mark == null) {
            System.out.println("no parkour checkpoint here!");
        } else {
            mark.onActivate(p);
        }
    }

    private ParkourMark getParkourLandmark(Location location) {
        for (Parkour p : parkours) {
            if (p.getLandmarkByPos(location) != null) {
                return p.getLandmarkByPos(location);
            }
        }
        return null;
    }

    public Parkour createParkour() {
        Parkour p = new Parkour(this);
        parkours.add(p);
        return p;
    }

    public int killAll(ExtronPlayer sender, EntityType type, int radius) {
        return killAll(sender.getLocation(),type,radius);
    }

    public int killAll(Location center, EntityType type, int radius) {
        int i = 0;
        for (Entity e : this.handle.getEntitiesByClass(type.getEntityClass())) {
            if (e.getLocation().distance(center) <= radius || radius == -1) {
                e.remove();
                i++;
            }
        }
        return i;
    }


    public void killAllEntities() {
        for (Entity e : this.handle.getEntities()) {
            if (!(e instanceof Player)) {
                e.remove();
            }
        }
    }

    public List<ExtronEntity> getEntities() {
        return entities;
    }

    public boolean isFreeLocation(ExtronPlayer p, int x, int z, boolean upToSky) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < z; j++) {
                int xpos = i - x/2;
                int zpos = j - z/2;
                Location loc = p.getLocation().add(xpos,0,zpos);
                if (loc.getBlock().getType() != Material.AIR) {
                    System.out.println("non air block " + loc.getBlock().getType() + " at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                    return false;
                }
                if (upToSky) {
                    if (handle.getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getY() > loc.getBlockY()) {
                        System.out.println("has higher block at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Spawns a particle for all players in this world
     * @param particle The NMS <code>EnumParticle</code> type to create
     * @param loc The location to spawn
     * @param xoff The x offset
     * @param yoff The y offset
     * @param zoff The z offset
     * @param speed The speed of the particle
     * @param count The amount of particles to create
     * @param o The optional int array of arguments
     */
    public void spawnParticle(EnumParticle particle, Location loc, double xoff, double yoff, double zoff, double speed, int count, Object o) {
        for (ExtronPlayer p : players) {
            p.handle.getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(particle, true, (float)loc.getX(),(float)loc.getY(),(float)loc.getZ(),(float)xoff,(float)yoff,(float)zoff,(float)speed,count,(int[])o));
        }
    }

    public void fillBlocks(BlockPos pos1, BlockPos pos2, Material m, int data, boolean hollow) {
        for (BlockPos pos : BlockPos.allPosesInCube(pos1,pos2,hollow)) {
            pos.setBlock(m,data);
        }
    }

    public MapCreator getMapCreator() {
        return mapCreator;
    }

    public void setMapCreator(MapCreator creator) {
        this.mapCreator = creator;
    }

    public boolean isMap() {
        return isMap;
    }

    public ExtronPlayer getClosestPlayer(ExtronEntity entity) {
        ExtronPlayer player = null;
        for (ExtronPlayer p : players) {
            if (player == null || p.getLocation().distance(entity.getEntity().getLocation()) < player.getLocation().distance(entity.getEntity().getLocation())) {
                player = p;
            }
        }
        return player;
    }

    public List<ExtronPlayer> getNearbyPlayers(ExtronEntity entity, double maxDistance) {
        List<ExtronPlayer> players = new ArrayList<>();
        for (ExtronPlayer p : this.players) {
            if (p.getLocation().distance(entity.getEntity().getLocation()) < maxDistance) {
                players.add(p);
            }
        }
        return players;
    }
}
