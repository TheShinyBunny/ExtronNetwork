package com.extron.network.api.inventory;

import com.extron.network.api.inventory.base.ExtronStack;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ButtonBase implements Button {

    private int slot;

    public ButtonBase(int slot) {
        this.slot = slot;
    }

    public ButtonBase() {
        this.slot = 0;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public ButtonBase addTo(InventoryMenu menu) {
        menu.addButton(slot,this);
        return this;
    }

    @Override
    public InteractAction getInteractAction() {
        return null;
    }

    @Override
    public ExtronStack createItem() {
        ExtronStack stack = new ExtronStack(this.getType(),this.getCount(), this.getDamage());
        if (this.getLore() != null) {
            stack.setLore(this.getLore());
        }
        if (this.getDisplayName() != null) {
            stack.setDisplayName(this.getDisplayName());
        }
        return stack;
    }
}
