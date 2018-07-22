package com.extron.network.api.inventory.interactions;

import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.inventory.InventoryMenu;

public class MenuOpenerInteract extends StackInteractAction {
    private final InventoryMenu menu;

    public MenuOpenerInteract(String id, InventoryMenu menu) {
        super(id);
        this.menu = menu;
    }

    public InventoryMenu getMenu() {
        return menu;
    }

    @Override
    public void onClick(ItemInteractEvent e) {
        e.getPlayer().openInventory(menu);
    }
}
