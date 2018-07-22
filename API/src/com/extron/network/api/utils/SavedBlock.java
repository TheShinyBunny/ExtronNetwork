package com.extron.network.api.utils;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.IInventory;
import net.minecraft.server.v1_8_R1.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SavedBlock {

    private BlockState state;

    public SavedBlock(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    public void replaceState() {
        Block b = state.getLocation().getBlock();
        b.setType(state.getType());
        b.setData(state.getData().getData());
        if (state instanceof InventoryHolder) {
            Inventory i = ((InventoryHolder) state).getInventory();
            IInventory inv = ((CraftInventory) i).getInventory();
            if (inv instanceof TileEntity) {
                ((CraftWorld)state.getWorld()).getHandle().setTileEntity(new BlockPosition(state.getX(),state.getY(),state.getZ()), (TileEntity) inv);
            }
        }
    }
}
