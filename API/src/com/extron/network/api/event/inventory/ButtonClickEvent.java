package com.extron.network.api.event.inventory;

import com.extron.network.api.event.ExtronEvent;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ButtonClickEvent extends ExtronEvent implements Cancellable {

    private InventoryMenu menu;
    private ItemStack item;
    private int slot;
    private ClickType type;

    public ButtonClickEvent(InventoryMenu menu, ItemStack item, int slot, ClickType type) {
        this.menu = menu;
        this.item = item;
        this.slot = slot;
        this.type = type;
    }

    public int getSlot() {
        return slot;
    }

    public ClickType getClickType() {
        return type;
    }

    public InventoryMenu getMenu() {
        return menu;
    }
    
    public ExtronPlayer getPlayer() {
        return menu.getOwner();
    }

    public ItemStack getItem() {
        return item;
    }

    public void allowTakeItem() {
        this.setCancelled(false);
    }
}
