package com.extron.network.api.game;

import com.extron.network.api.inventory.InteractAction;
import com.extron.network.api.inventory.SimpleButton;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;

public abstract class CreatorButton extends SimpleButton {

    public CreatorButton(Material type, String name) {
        super(0, type);
        this.setDisplayName(name);
    }

    @Override
    public InteractAction getInteractAction() {
        return (e)->{
            boolean b = false;
            e.getPlayer().getMapCreator().setActivePlayer(e.getPlayer());
            if (e.getBlock() != null) {
                b = blockInteract(e.getBlock(),e.getAction());
            }
            if (e.getEntity() != null) {
                b = entityInteract(e.getEntity());
            }
            if (e.getEntity() == null && e.getBlock() == null) {
                b = airInteract(e.getAction());
            }
            e.setCancelled(b);
        };
    }

    public boolean airInteract(Action action) {
        return false;
    }

    public boolean entityInteract(Entity e) {
        return false;
    }

    public boolean blockInteract(Block clicked, Action action) {
        return false;
    }

    public boolean blockPlace(Block clicked, Block placed) {
        return false;
    }

    public boolean blockBreak(Block block) {
        return false;
    }
}
