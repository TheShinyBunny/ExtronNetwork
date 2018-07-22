package com.extron.network.api.inventory;

import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.players.ExtronPlayer;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ClickAction {

    static ClickAction open(Supplier<InventoryMenu> menu) {
        return (e)->e.getPlayer().openInventory(menu.get());
    }

    static ClickAction open(Function<ExtronPlayer, InventoryMenu> menuWithPlayer) {
        return (e)->e.getPlayer().openInventory(menuWithPlayer.apply(e.getPlayer()));
    }

    static ClickAction open(Function<ExtronPlayer, InventoryMenu> menuWithPlayer, ExtronPlayer thePlayer) {
        return (e)->e.getPlayer().openInventory(menuWithPlayer.apply(thePlayer));
    }

    void onClick(ButtonClickEvent e);

}
