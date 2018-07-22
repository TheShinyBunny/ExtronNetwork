package com.extron.network.api.event.inventory;

import com.extron.network.api.event.ExtronEvent;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class ItemInteractEvent extends ExtronEvent implements Cancellable {

    private final Entity entity;
    private final Block block;
    private final Action action;
    private ItemStack item;
    private ExtronPlayer player;
    private String cancelMessage;

    public ItemInteractEvent(ExtronPlayer player, ItemStack item, Entity entity, Block block, Action action) {
        this.item = item;
        this.entity = entity;
        this.block = block;
        this.player = player;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }

    public ExtronPlayer getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public void allowInteraction() {
        this.setCancelled(false);
    }

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
        this.setCancelled(cancelMessage != null);
    }

    @Override
    public void setCancelled(boolean cancel) {
        System.out.println("item interact set cancelled = " + cancel);
        super.setCancelled(cancel);
    }
}
