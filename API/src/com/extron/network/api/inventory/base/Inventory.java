package com.extron.network.api.inventory.base;

import com.extron.network.api.utils.ListUtils;
import net.minecraft.server.v1_8_R1.IInventory;
import net.minecraft.server.v1_8_R1.ItemStack;

import java.util.Collection;

public class Inventory {

    protected IInventory handle;

    public Inventory(IInventory handle) {
        this.handle = handle;
    }

    public int getSize() {
        return handle.getSize();
    }

    public String getTitle() {
        return handle.getName();
    }

    public void setItem(int i, ExtronStack stack) {
        this.handle.setItem(i,stack == null ? null : stack.toNMSItem());
    }

    public ExtronStack getItem(int i) {
        ItemStack stack = handle.getItem(i);
        return stack == null ? null : new ExtronStack(stack);
    }

    public void addItem(ExtronStack stack) {
        if (this.hasEmptySlots()) {
            this.setItem(getFirstOpenSlot(0), stack);
        }
    }

    public void addItem(ExtronStack stack, int fromSlot) {
        if (this.hasEmptySlots() && getFirstOpenSlot(fromSlot) != -1) {
            this.setItem(getFirstOpenSlot(fromSlot), stack);
        }
    }

    public boolean hasEmptySlots() {
        for (ExtronStack stack : getItems()) {
            if (stack == null) {
                return true;
            }
        }
        return false;
    }

    public int getFirstOpenSlot(int from) {
        if (this.isEmpty()) {
            return from;
        }
        int i = from;
        while (true) {
            if (getItem(i) == null) {
                return i;
            }
            if (i >= getSize()) {
                return -1;
            }
            i++;
        }
    }

    public boolean isEmpty() {
        for (ExtronStack stack : getItems()) {
            if (stack != null) {
                return false;
            }
        }
        return true;
    }

    public Collection<ExtronStack> getItems() {
        return ListUtils.convertAllArray(handle.getContents(),s->s == null ? null : new ExtronStack(s));
    }

    public IInventory getHandle() {
        return handle;
    }

    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            this.setItem(i,null);
        }
    }
}
