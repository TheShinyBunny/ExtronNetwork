package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.entity.ExtronFallingBlock;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FireWaves implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        List<BlockFace> faces = new ArrayList<>();
        for (BlockFace f : BlockFace.values()) {
            if (f.getModY() == 0 && (f.getModX() == 0 || f.getModZ() == 0) && f != BlockFace.SELF) {
                faces.add(f);
            }
        }
        Location loc = p.getLocation();
        new ExtronRunnable() {
            int i = 0;

            @Override
            public void run() {
                for(BlockFace f :faces){
                    Location loc2 = loc.getBlock().getRelative(f, i + 1).getLocation().add(0, 0.7, 0);
                    ExtronFallingBlock fb = new ExtronFallingBlock(p.getWorld(), loc2, Material.FIRE, (byte) 0, false, new Vector(0, 0, 0));
                    fb.spawn();
                }
                i++;
            }
        }.repeat(10,2,8);
    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {

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
        return Rarity.RARE;
    }

    @Override
    public boolean foundInBasicLoot() {
        return true;
    }

    @Override
    public String getId() {
        return "fire_waves";
    }

    @Override
    public String getDisplayName() {
        return "Fire Waves";
    }

    @Override
    public Material getIcon() {
        return Material.FIREBALL;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Summons waves of fire around you in all directions!";
    }
}
