package com.extron.network.api.game.listeners;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface GameEventListener {

    int onFoodLost(ExtronPlayer p, int level);

    boolean onPlaceBlock(ExtronPlayer p, Block placed, ItemStack itemInHand);

    int onBreakBlock(ExtronPlayer p, Block broken, int xp);

    float onExplosion(Entity entity, Location location, List<Block> blocks, float yield);

    boolean onItemPickup(ExtronPlayer p, Item item, int remaining);

    boolean onItemDropped(ExtronPlayer p, Item dropped);

    int onPickupXP(ExtronPlayer p, int amount);

}
