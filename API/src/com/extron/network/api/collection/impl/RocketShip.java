package com.extron.network.api.collection.impl;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.entity.EntityRocketShip;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.building.Structure;
import com.extron.network.api.utils.building.StructureBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class RocketShip implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        System.out.println("rocket ship!");
        StructureBuilder rocket = new StructureBuilder(3,7,3)
                .addLayer("   "," a ","   ")
                .addLayer("   "," r ","   ")
                .addLayer("   "," i ","   ")
                .addLayer("   "," i ","   ")
                .addLayer(" 0 ","1i1"," 0 ")
                .addLayer(" f ","fif"," f ")
                .addLayer(" f ","f f"," f ")

                .addMaterial('f',Material.FENCE)
                .addMaterial('i',Material.IRON_BLOCK)
                .addMaterial('r',Material.REDSTONE_BLOCK)
                .addMaterial('a',Material.ANVIL)
                .addMaterial('0',Material.FENCE_GATE)
                .addMaterial('1',Material.FENCE_GATE,1);
        Structure s = rocket.build(p.getLocation().subtract(1,0,1));
        EntityRocketShip rocketShip = new EntityRocketShip(p.getWorld(),s,p);
        rocketShip.spawn();
    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {

    }

    @Override
    public boolean foundInBasicLoot() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {
        ExtronPlayer p = e.getPlayer();
        if (!p.getWorld().isFreeLocation(p,3,3,true)) {
            e.setCancelMessage(ChatColor.RED + "Not enough space to spawn the rocket ship!");
        }
    }

    @Override
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }

    @Override
    public String getId() {
        return "rocket";
    }

    @Override
    public String getDisplayName() {
        return "Rocket Ship";
    }

    @Override
    public Material getIcon() {
        return Material.FIREWORK;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Ride on a little rocket ship up to the sky!";
    }
}
