package com.extron.network.api.hologram;

import com.extron.network.api.Main;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hologram {

    protected Location loc;
    private List<String> lines;
    protected double lineDistance;

    protected List<ArmorStand> armorStands;
    protected boolean visible;
    private boolean asVisible;

    public Hologram(Location loc) {
        this.loc = loc;
        this.visible = false;
        this.lineDistance = 0.28;
        this.lines = new ArrayList<>();
        this.armorStands = new ArrayList<>();
        Main.holograms.add(this);
    }

    public static Hologram create(Location loc, String... lines) {
        Hologram h = new Hologram(loc);
        h.addAllLines(Arrays.asList(lines));
        h.spawn();
        return h;
    }

    public void addLine(String line) {
        lines.add(line);
        if (visible) {
            reload();
        }
    }

    public void setLine(int i, String newLine) {
        if (lines.size() <= i) {
            lines.add(newLine);
        } else {
            lines.set(i, newLine);
        }
        if (visible) {
            reload();
        }
    }

    public void removeLine(int i) {
        lines.remove(i);
        if (visible) {
            reload();
        }
    }

    public void clearLines(){
        lines.clear();
        if (visible) {
            reload();
        }
    }

    public boolean spawn() {
        if (this.visible) {
            System.out.println("hologram already visible!");
            return false;
        }
        Location current = loc.clone();
        for (String line : lines) {
            ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(current, EntityType.ARMOR_STAND);
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
            as.remove();
        }
        visible = false;
        return true;
    }

    public List<String> getLines() {
        return lines;
    }

    public Location getLocation() {
        return loc;
    }

    public double getLineDistance() {
        return lineDistance;
    }

    public void addAllLines(List<String> lines) {
        this.lines.addAll(lines);
    }

    public void reload() {
        this.despawn();
        this.spawn();
    }

    public void display(ExtronPlayer sender) {
        sender.sendMessage("---------------------");
        for (String s : this.getLines()) {
            sender.sendMessage("    " + s);
        }
        sender.sendMessage("---------------------");
    }
}
