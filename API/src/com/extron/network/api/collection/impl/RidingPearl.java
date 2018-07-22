package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.entity.ExtronEntity;
import com.extron.network.api.entity.SeatablePearl;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class RidingPearl implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        ExtronEntity prev = p.getRidingEntity();
        if (prev instanceof SeatablePearl) {
            prev.kill();
        }
        SeatablePearl pearl = new SeatablePearl(p);
        pearl.spawn();
        p.reloadInventory();
    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {

    }

    @Override
    public int getCooldown() {
        return 2;
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {

    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean foundInBasicLoot() {
        return true;
    }

    @Override
    public String getId() {
        return "riding_pearl";
    }

    @Override
    public String getDisplayName() {
        return "Riding Pearl";
    }

    @Override
    public Material getIcon() {
        return Material.ENDER_PEARL;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "A nice pearl that will make you fly with it!";
    }
}
