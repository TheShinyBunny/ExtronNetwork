package com.extron.network.api.utils.building;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Structure {

    private Location origin;
    private List<Piece> pieces;

    public Structure(Location origin) {
        this.origin = origin.clone();
        pieces = new ArrayList<>();
    }

    public Location getOrigin() {
        return origin;
    }

    public Structure append(Material type, int data, int xoff, int yoff, int zoff) {
        Piece p = new Piece(type,origin,data,xoff,yoff,zoff);
        pieces.add(p);
        return this;
    }

    public Structure append(Material type, int xoff, int yoff, int zoff) {
        return append(type,0,xoff,yoff,zoff);
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public static class Piece {

        private int zoff;
        private int yoff;
        private int xoff;
        private Material type;
        private int data;
        private Location mark;

        public Piece(Material type, Location origin, int data, int xoff, int yoff, int zoff) {
            this.type = type;
            this.data = data;
            this.xoff = xoff;
            this.yoff = yoff;
            this.zoff = zoff;
            this.mark = origin.clone();
        }

        public Location getLocation() {
            return new Location(mark.getWorld(),mark.getBlockX() + xoff, mark.getBlockY() + yoff, mark.getBlockZ() + zoff);
        }

        public Material getType() {
            return type;
        }

        public byte getData() {
            return (byte) data;
        }
    }

}
