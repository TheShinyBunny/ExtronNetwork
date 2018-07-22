package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.entity.ExtronFallingBlock;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class CakeFountain implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        new ExtronRunnable() {
            int c = 0;
            @Override
            public void run() {
                if (c > 10) {
                    this.cancel();
                    return;
                }
                ExtronFallingBlock efb = new ExtronFallingBlock(p.getWorld(),p.getLocation(),Material.CAKE_BLOCK,(byte)0,false, new Vector((Math.random() - Math.random()) / 3,0.8,(Math.random() - Math.random()) / 3));
                efb.spawn();
                c++;
            }
        }.timer(2,8);
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
        return 20;
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {

    }

    @Override
    public Rarity getRarity() {
        return Rarity.EPIC;
    }

    @Override
    public String getId() {
        return "cake_fountain";
    }

    @Override
    public String getDisplayName() {
        return "Cake Fountain";
    }

    @Override
    public Material getIcon() {
        return Material.CAKE;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Spawns a bunch of flying cakes around you!";
    }

}
