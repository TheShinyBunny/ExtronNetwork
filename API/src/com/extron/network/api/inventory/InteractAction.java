package com.extron.network.api.inventory;

import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface InteractAction {

    static InteractAction open(Supplier<InventoryMenu> menu) {
        return (e)->e.getPlayer().openInventory(menu.get());
    }

    static InteractAction open(Function<ExtronPlayer, InventoryMenu> menuWithPlayer) {
        return (e)->e.getPlayer().openInventory(menuWithPlayer.apply(e.getPlayer()));
    }

    static InteractAction open(Function<ExtronPlayer, InventoryMenu> menuWithPlayer, ExtronPlayer thePlayer) {
        return (e)->e.getPlayer().openInventory(menuWithPlayer.apply(thePlayer));
    }

    void interact(ItemInteractEvent e);
}
