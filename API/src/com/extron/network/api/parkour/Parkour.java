package com.extron.network.api.parkour;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.hologram.SavedHologram;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single parkour mission.
 */
public class Parkour {

    private final ExtronWorld world;
    private List<ParkourMark> landmarks;
    private int id;

    public Parkour(ExtronWorld world) {
        this.world = world;
        this.landmarks = new ArrayList<>();
        this.id = world.getParkours().size();
    }

    public List<ParkourMark> getLandmarks() {
        return landmarks;
    }

    public ParkourMark getLandmarkByPos(Location location) {
        for (ParkourMark lm : landmarks) {
            if (lm.getLocation().getBlock().equals(location.getBlock())) {
                return lm;
            }
        }
        return null;
    }

    public ParkourMark getStart() {
        for (ParkourMark lm : landmarks) {
            if (lm.getType() == ParkourMark.Type.START) {
                return lm;
            }
        }
        return null;
    }

    public ParkourMark getEnd() {
        for (ParkourMark lm : landmarks) {
            if (lm.getType() == ParkourMark.Type.END) {
                return lm;
            }
        }
        return null;
    }

    /**
     * Adds a checkpoint. if this is the last checkpoint added, it will be the end of the parkour.
     *
     * @param loc The location of the new checkpoint
     */
    public void addCheckpoint(Location loc) {
        if (this.getEnd() != null) {
            this.getEnd().update(landmarks.size() - 1);
        }
        ParkourMark mark = new ParkourMark(this,loc,landmarks.size());
        landmarks.add(mark);
        updateConfig();
    }

    public void updateConfig() {
        world.getConfig().set("parkours." + this.id, null);
        for (ParkourMark m : landmarks) {
            world.getConfig().setLocation("parkours." + this.id + "." + m.getNumber() + ".location", m.getLocation(),false,false);
            world.getConfig().set("parkours." + this.id + "." + m.getNumber() + ".hologram",m.getHologram().getUUID().toString());
        }
        world.getConfig().save();
    }

    /**
     * Removes a certain checkpoint from the parkour course. Will shift all following checkpoints 1 back.
     * If the deleted checkpoint is the end, will set the end to the last checkpoint.
     * If the deleted checkpoint is the start, will set the start to the next checkpoint.
     * @param number The nunber position of the checkpoint to delete
     * @return Whether it's found a checkpoint at number to remove
     */
    public boolean removeCheckpoint(int number) {
        if (getLandmark(number) == null) {
            return false;
        }
        ParkourMark pm = getLandmark(number);
        landmarks.remove(pm);
        for (ParkourMark lm : landmarks) {
            if (lm.getNumber() > pm.getNumber()) {
                lm.update(lm.getNumber() - 1);
            }
            if (lm.isBefore(pm)) {
                lm.update(lm.getNumber());
            }
        }
        pm.remove();
        updateConfig();
        return true;
    }

    /**
     * Returns the <code>ParkourMark</code> located on the specified position.
     * @param number The landmark position number to search for
     * @return The landmark at that position
     */
    public ParkourMark getLandmark(int number) {
        for (ParkourMark lm : landmarks) {
            if (lm.getNumber() == number) {
                return lm;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void sendInfo(ExtronPlayer p) {
        for (ParkourMark pm : landmarks) {
            p.sendMessage(pm.toString());
        }
    }

    public void loadCheckpoint(int i, Location loc, SavedHologram h) {
        if (this.getEnd() != null) {
            this.getEnd().update(landmarks.size() - 1);
        }
        ParkourMark m = new ParkourMark(this,loc,i,h);
        this.landmarks.add(m);
    }

    public ExtronWorld getWorld() {
        return world;
    }

    public int getLastIndex() {
        return landmarks.size() - 1;
    }
}
