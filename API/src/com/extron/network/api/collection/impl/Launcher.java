package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Launcher implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        p.handle.setVelocity(new Vector(0,1.8,0));
    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {

    }

    @Override
    public boolean foundInBasicLoot() {
        return true;
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {

    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public String getId() {
        return "launcher";
    }

    @Override
    public String getDisplayName() {
        return "Launcher";
    }

    @Override
    public Material getIcon() {
        return Material.SLIME_BLOCK;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Launches you up into the air";
    }
}
