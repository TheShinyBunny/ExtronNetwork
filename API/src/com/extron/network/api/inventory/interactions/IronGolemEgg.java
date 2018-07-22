package com.extron.network.api.inventory.interactions;

import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.inventory.base.ExtronStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class IronGolemEgg extends ExtronStack {
    public IronGolemEgg() {
        super(Material.MONSTER_EGG);
        this.setDisplayName("Spawn Iron Golem");
        this.setInteractAction(new Interaction());
    }

    private static class Interaction extends StackInteractAction {

        Interaction() {
            super("iron_golem_egg");
        }

        @Override
        public void onClick(ItemInteractEvent e) {
            if (e.getBlock() != null) {
                Location loc = e.getBlock().getLocation().add(0,1,0);
                e.getPlayer().getWorld().spawnEntity(loc,EntityType.IRON_GOLEM);
            }
        }
    }
}
