package com.extron.network.api.utils;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.config.Config;
import net.minecraft.server.v1_8_R1.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockPos implements Cloneable {

    private final ExtronWorld world;
    private int x;
    private int y;
    private int z;

    public BlockPos(ExtronWorld world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(Location loc) {
        this(Main.getWorld(loc.getWorld()),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
    }

    public BlockPos(Block b) {
        this(Main.getWorld(b.getWorld()),b.getX(),b.getY(),b.getZ());
    }

    public BlockPos(BlockPos pos) {
        this(pos.world,pos.x,pos.y,pos.z);
    }

    public static BlockPos of(Block b) {
        return new BlockPos(b);
    }

    public static List<BlockPos> allPosesInCube(BlockPos pos1, BlockPos pos2, boolean hollow) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos pos3 = BlockPos.getMinimum(pos1,pos2);
        BlockPos pos4 = BlockPos.getMaximum(pos1,pos2);
        for (int i = 0; i <= pos4.x - pos3.x; i++) {
            for (int j = 0; j <= pos4.y - pos3.y; j++) {
                for (int k = 0; k <= pos4.z - pos3.z; k++) {
                    if (!hollow || i == 0 || i == pos4.x - pos3.x || j == 0 || j == pos4.y - pos3.y || k == 0 || k == pos4.z - pos3.z) {
                        list.add(new BlockPos(pos3.clone().add(i, j, k)));
                    }
                }
            }
        }
        return list;
    }

    private static BlockPos getMaximum(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(pos1.world,Math.max(pos1.x,pos2.x),Math.max(pos1.y,pos2.y),Math.max(pos1.z,pos2.z));
    }

    private static BlockPos getMinimum(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(pos1.world,Math.min(pos1.x,pos2.x),Math.min(pos1.y,pos2.y),Math.min(pos1.z,pos2.z));
    }

    public ExtronWorld getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Block getBlock() {
        return world.handle.getBlockAt(x,y,z);
    }

    public Location toLocation() {
        return new Location(world.handle,x+0.5,y+0.5,z+0.5);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPos)) return false;
        BlockPos other = (BlockPos) obj;
        return other.world.equals(world) && this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return world.getName() + "," + x + "," + y + "," + z;
    }

    public String toNiceString() {
        return String.format("x=%d, y=%d, z=%d in world %s",x,y,z,world.getName());
    }

    public static BlockPos fromString(String str) {
        Location loc = Config.parseLocation(str,null,null,false);
        if (loc == null) return null;
        return new BlockPos(loc);
    }

    public double distanceTo(BlockPos other) {
        return this.toLocation().distance(other.toLocation());
    }

    public BlockPos up() {
        return up(1);
    }

    public BlockPos down() {
        return this.down(1);
    }

    public BlockPos down(int i) {
        return this.up(-i);
    }

    public BlockPos up(int i) {
        return this.add(0,i,0);
    }

    public BlockPos add(int x, int y, int z) {
        this.x+=x;
        this.y+=y;
        this.z+=z;
        return this;
    }

    public BlockPos subtract(int x, int y, int z) {
        return add(-x,-y,-z);
    }

    @Override
    public BlockPos clone() {
        return new BlockPos(world,x,y,z);
    }

    public void setBlock(Material m, int data) {
        getBlock().setType(m);
        getBlock().setData((byte) data);
    }
}
