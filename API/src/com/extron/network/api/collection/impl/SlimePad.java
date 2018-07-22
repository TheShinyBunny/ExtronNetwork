package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.entity.SlimePadEntity;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class SlimePad implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        List<SlimePadEntity> entities = new ArrayList<>();
        Location loc = p.getLocation();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                SlimePadEntity e = new SlimePadEntity(p.getWorld());
                e.setSpawnLocation(loc.clone().add(i * 0.5,-1.45,j * 0.5));
                e.spawn();
                entities.add(e);
            }
        }
        new ExtronRunnable() {
            @Override
            public void run() {
                for (SlimePadEntity en : entities) {
                    en.kill();
                }
            }
        }.delay(400);
    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {

    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {

    }

    @Override
    public Rarity getRarity() {
        return Rarity.EPIC;
    }

    @Override
    public boolean foundInBasicLoot() {
        return true;
    }

    @Override
    public String getId() {
        return "slime_pad";
    }

    @Override
    public String getDisplayName() {
        return "Slime Pad";
    }

    @Override
    public Material getIcon() {
        return Material.SLIME_BALL;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Spawns a limited-time bouncy slime pad for everyone to enjoy!";
    }
}
