package com.extron.network.api.hologram;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SavedHologram {

    protected UUID uuid;
    protected Location loc;
    protected ExtronWorld world;
    private List<String> lines;
    protected double lineDistance;

    protected List<ArmorStand> armorStands;
    protected boolean visible;
    private boolean asVisible;

    public SavedHologram(ExtronWorld world, Location loc, UUID id) {
        this.world = world;
        this.loc = loc;
        this.uuid = id;
        this.visible = false;
        this.lineDistance = 0.28;
        this.lines = new ArrayList<>();
        this.armorStands = new ArrayList<>();
        world.getConfig().setLocation("holograms." + id.toString() + ".location",loc,false,false);
        world.getConfig().save();
    }

    public SavedHologram(UUID uuid) {
        this.uuid = uuid;
        this.lineDistance = 0.28;
    }

    public void addLine(String line) {
        lines.add(line);
        world.getConfig().set("holograms." + this.uuid.toString() + ".lines", lines);
        world.getConfig().save();
    }

    public void setLine(int i, String newLine) {
        if (lines.size() <= i) {
            lines.add(newLine);
        } else {
            lines.set(i, newLine);
        }
        world.getConfig().set("holograms." + this.uuid.toString() + ".lines", lines);
        world.getConfig().save();
    }

    public void removeLine(int i) {
        lines.remove(i);
        world.getConfig().set("holograms." + this.uuid.toString() + ".lines", lines);
        world.getConfig().save();
    }

    public void clearLines(){
        lines.clear();
        world.getConfig().set("holograms." + this.uuid.toString() + ".lines", new ArrayList<>());
        world.getConfig().save();
    }

    public boolean spawn() {
        if (this.visible) {
            System.out.println("hologram already visible!");
            return false;
        }
        Location current = loc.clone();
        for (String line : lines) {
            ArmorStand as = (ArmorStand) world.spawnEntity(current, EntityType.ARMOR_STAND);
            as.setCustomName(line);
            as.setCustomNameVisible(true);
            as.setGravity(false);
            as.setVisible(isArmorStandVisible());
            as.setSmall(true);
            this.armorStands.add(as);
            current.subtract(0,lineDistance,0);
        }
        visible = true;
        return true;
    }

    public void setArmorStandVisible(boolean visible) {
        this.asVisible = visible;
    }

    public boolean isArmorStandVisible() {
        return asVisible;
    }

    public boolean despawn() {
        if (!this.visible) {
            System.out.println("hologram already hidden!");
            return false;
        }
        for (ArmorStand as : armorStands) {
            System.out.println("removed armor stand");
            as.remove();
        }
        visible = false;
        return true;
    }

    public ExtronWorld getWorld() {
        return world;
    }

    public List<String> getLines() {
        return lines;
    }

    public Location getLocation() {
        return loc;
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getLineDistance() {
        return lineDistance;
    }

    public void addAllLines(List<String> lines) {
        this.lines.addAll(lines);
        world.getConfig().set("holograms." + this.uuid.toString() + ".lines", this.lines);
        world.getConfig().save();
    }

    public void reload() {
        this.despawn();
        this.spawn();
    }

    public void delete() {
        this.despawn();
        this.world.getConfig().set("holograms." + uuid.toString(),null);
        world.getConfig().save();
        this.world.getHolograms().remove(this);
    }

    public void display(ExtronPlayer sender) {
        sender.sendMessage("---------------------");
        for (String s : this.getLines()) {
            sender.sendMessage("    " + s);
        }
        sender.sendMessage("---------------------");
    }
}
