package com.extron.network.api.parkour;


import com.extron.network.api.hologram.SavedHologram;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class ParkourMark {

    private SavedHologram hologram;

    public enum Type {
        START, CHECKPOINT, END;
    }

    private Location location;
    private Parkour parkour;
    private Type type;

    private int number;

    public ParkourMark(Parkour p, Location loc, int n) {
        this.parkour = p;
        this.location = loc;
        this.number = n;
        if (n == 0) {
            this.type = Type.START;
            this.hologram = p.getWorld().createHologram(ChatColor.AQUA + "" + ChatColor.BOLD + "Parkour Mission",loc.getBlock().getLocation().add(0.5,1,0.5));
            this.hologram.addLine(ChatColor.GREEN + "START");
            loc.getBlock().setType(Material.IRON_PLATE);
        } else if (n < p.getLandmarks().size()) {
            this.type = Type.CHECKPOINT;
            this.hologram = p.getWorld().createHologram(ChatColor.GREEN + "" + ChatColor.BOLD + "CHECKPOINT",loc.getBlock().getLocation().add(0.5,1,0.5));
            this.hologram.addLine(ChatColor.AQUA + "#" + n);
            loc.getBlock().setType(Material.GOLD_PLATE);
        } else {
            this.type = Type.END;
            this.hologram = p.getWorld().createHologram(ChatColor.AQUA + "" + ChatColor.BOLD + "Parkour Mission",loc.getBlock().getLocation().add(0.5,1,0.5));
            this.hologram.addLine(ChatColor.RED + "END");
            loc.getBlock().setType(Material.IRON_PLATE);
        }
        hologram.reload();
    }

    public ParkourMark(Parkour p, Location loc, int n, SavedHologram h) {
        this.parkour = p;
        this.location = loc;
        this.number = n;
        this.hologram = h;
        this.update(number);
    }

    public Parkour getParkour() {
        return parkour;
    }

    public Type getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public void update(int n) {
        this.number = n;
        if (n == 0) {
            this.type = Type.START;
            this.hologram.setLine(0,ChatColor.AQUA + "" + ChatColor.BOLD + "Parkour Mission");
            if (this.hologram.getLines().size() == 2) {
                this.hologram.setLine(1,ChatColor.GREEN + "START");
            } else {
                this.hologram.addLine(ChatColor.GREEN + "START");
            }
            location.getBlock().setType(Material.IRON_PLATE);
        } else if (n < parkour.getLandmarks().size()) {
            this.type = Type.CHECKPOINT;
            this.hologram.setLine(0,ChatColor.GREEN + "" + ChatColor.BOLD + "CHECKPOINT");
            if (this.hologram.getLines().size() == 2) {
                this.hologram.setLine(1,ChatColor.AQUA + "#" + n);
            } else {
                this.hologram.addLine(ChatColor.AQUA + "#" + n);
            }
            location.getBlock().setType(Material.GOLD_PLATE);
        } else {
            this.type = Type.END;
            this.hologram.setLine(0,ChatColor.AQUA + "" + ChatColor.BOLD + "Parkour Mission");
            if (this.hologram.getLines().size() == 2) {
                this.hologram.setLine(1,ChatColor.RED + "END");
            } else {
                this.hologram.addLine(ChatColor.RED + "END");
            }
            location.getBlock().setType(Material.IRON_PLATE);
        }
        hologram.reload();
    }

    /**
     * Called when a player steps on this landmark's pressure plate
     * @param p The player that stepped
     */
    public void onActivate(ExtronPlayer p) {
        switch (this.type) {
            case START:
                p.startParkour(this.parkour);
                break;
            case CHECKPOINT:
                p.parkourCheckpoint(this.parkour,this);
                break;
            case END:
                p.finishedParkour(this.parkour);
                break;
        }
    }

    public boolean isBefore(ParkourMark other) {
        return this.number + 1 == other.number;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getNumber() {
        return number;
    }

    public void remove() {
        this.hologram.delete();
        this.location.getBlock().setType(Material.AIR);
    }

    public SavedHologram getHologram() {
        return hologram;
    }

    @Override
    public String toString() {
        return "Landmark: index=" + this.number + ", type=" + this.type;
    }
}
