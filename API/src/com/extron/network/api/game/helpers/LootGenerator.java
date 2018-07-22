package com.extron.network.api.game.helpers;

import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.utils.BlockPos;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootGenerator {

    private final int minItems;
    private final int maxItems;
    private List<LootEntry> entries;
    private List<BlockPos> chests;
    private LootPopulator populator;
    private int bonus;

    public LootGenerator() {
        this(0,1);
    }

    public LootGenerator(int minItems, int maxItems) {
        this.minItems = minItems;
        this.maxItems = maxItems;
        this.entries = new ArrayList<>();
        this.chests = new ArrayList<>();
    }

    public LootGenerator addEntry(Material m, int weight) {
        return this.addEntry(m,1,weight);
    }

    public LootGenerator addEntry(Material m, int count, int weight) {
        return this.addEntry(m,count,0,weight);
    }

    public LootGenerator addEntry(Material m, int count, int data, int weight) {
        this.entries.add(new LootEntry(new ExtronStack(m,count, (short) data),weight));
        return this;
    }

    public void addChest(BlockPos loc) {
        this.chests.add(loc);
    }

    public List<ExtronStack> generateLoot() {
        System.out.println("generating loot");
        if (populator == null && entries.isEmpty()) {
            return new ArrayList<>();
        }
        if (entries.isEmpty()) {
            System.out.println("using LootPopulator");
            populate();
        }
        List<ExtronStack> items = new ArrayList<>();
        int i = minItems;
        System.out.println("Generating items");
        Random r = new Random();
        for (LootGroup group : LootGroup.values()) {
            int max = group.getMin() + (group.getAdditional() < 2 ? group.getAdditional() : r.nextInt(group.getAdditional()));
            for (int j = group.getMin(); j <= max + bonus; j++) {
                LootEntry entry = ListUtils.weightedRandomItem(ListUtils.filter(entries,e->e.getGroup()==group),LootEntry::getWeight);
                if (entry != null) {
                    items.add(entry.getStack());
                    i++;
                }
                if (i > maxItems) {
                    break;
                }
            }
            if (i > maxItems) {
                break;
            }
        }
        return items;
    }

    public void populate() {
        if (populator != null) {
            populator.populate(e -> entries.add(e));
        }
    }

    public void populateChests(LootPopulator populator) {
        this.populator = populator;
        List<ExtronStack> items = generateLoot();
        int i = 0;
        int total = items.size();
        int count = chests.size();
        for (BlockPos loc : chests) {
            System.out.println("adding items to chest");
            List<ExtronStack> devided = ListUtils.drainRandomly(items,i,count,total / 6);
            insertItemsRandomly(devided,loc);
            i++;
        }
    }

    public static void insertItemsRandomly(List<ExtronStack> items, BlockPos loc) {
        System.out.println("inserting randomly");
        Block b = loc.getBlock();
        if (b.getType() != Material.CHEST) {
            System.out.println("No chest for loot at specified location " + loc);
            return;
        }
        Chest c = (Chest) b.getState();
        Inventory inv = c.getBlockInventory();
        inv.clear();
        for (ExtronStack stack : items) {
            int i = getRandomEmptySlot(inv);
            if (i != -1) {
                inv.setItem(i, stack.toBukkitStack());
            }
        }
    }

    private static int getRandomEmptySlot(Inventory inv) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                list.add(i);
            }
        }
        Integer i = ListUtils.randomItem(list);
        if (i == null) return -1;
        return i;
    }

    public void setPopulator(LootPopulator populator) {
        this.populator = populator;
    }

    public void withItemBonus(int i) {
        this.bonus = i;
    }
}
