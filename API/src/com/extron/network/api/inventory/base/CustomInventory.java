package com.extron.network.api.inventory.base;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class CustomInventory extends Inventory {

    public CustomInventory(int size) {
        this(size,"Inventory");
    }

    public CustomInventory(int size, String title) {
        super(new InvWrapper(size,title));
    }

    public CustomInventory() {
        super(null);
    }

    public CustomInventory(IInventory handle) {
        super(handle);
    }

    protected static class InvWrapper implements IInventory {
        private final ItemStack[] items;
        private final String title;

        public InvWrapper(int size, String title) {
            this.items = new ItemStack[size];
            this.title = title;
        }

        @Override
        public int getSize() {
            return items.length;
        }

        @Override
        public ItemStack getItem(int i) {
            return items[i];
        }

        @Override
        public ItemStack splitStack(int i, int j) {
            ItemStack stack = this.getItem(i);
            if (stack == null) {
                return null;
            }
            ItemStack result;
            if (stack.count <= j) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = ItemStackHelper.copyNMSItem(stack, j);
                stack.count -= j;
            }

            this.update();
            return result;
        }

        @Override
        public ItemStack splitWithoutUpdate(int i) {
            ItemStack stack = this.getItem(i);
            if (stack == null) {
                return null;
            }
            ItemStack result;
            if (stack.count <= 1) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = ItemStackHelper.copyNMSItem(stack, 1);
                stack.count--;
            }

            return result;
        }

        @Override
        public void setItem(int i, ItemStack stack) {
            this.items[i] = stack;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public void update() {

        }

        @Override
        public boolean a(EntityHuman entityHuman) {
            return true;
        }

        @Override
        public void startOpen(EntityHuman entityHuman) {

        }

        @Override
        public void closeContainer(EntityHuman entityHuman) {

        }

        @Override
        public boolean b(int i, ItemStack itemStack) {
            return true;
        }

        @Override
        public int getProperty(int i) {
            return 0;
        }

        @Override
        public void b(int i, int i1) {

        }

        @Override
        public int g() {
            return 0;
        }

        @Override
        public void l() {

        }

        @Override
        public ItemStack[] getContents() {
            return new ItemStack[0];
        }

        @Override
        public void onOpen(CraftHumanEntity craftHumanEntity) {

        }

        @Override
        public void onClose(CraftHumanEntity craftHumanEntity) {

        }

        @Override
        public List<HumanEntity> getViewers() {
            return new ArrayList<>();
        }

        @Override
        public InventoryHolder getOwner() {
            return null;
        }

        @Override
        public void setMaxStackSize(int i) {

        }

        @Override
        public String getName() {
            return title;
        }

        @Override
        public boolean hasCustomName() {
            return title != null;
        }

        @Override
        public IChatBaseComponent getScoreboardDisplayName() {
            return new ChatComponentText(this.title);
        }
    }

}
