package com.extron.network.api.inventory;

import com.extron.network.api.collection.impl.StonePaperShears;
import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.inventory.base.CustomInventory;
import com.extron.network.api.inventory.base.Inventory;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.MathUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class InventoryMenu {

    /**
     * The player that owns this menu. Should be a different menu instance for each player.
     */
    protected ExtronPlayer owner;

    /**
     * The list of slots and their buttons.
     */
    protected Map<Integer,Button> buttons;
    public boolean refreshing;

    public InventoryMenu() {
        buttons = new HashMap<>();
    }

    /**
     * Initialize here the buttons of this menu.
     */
    public abstract void init();

    /**
     *
     * @return The title of the inventory
     */
    public abstract String getTitle();

    /**
     * @return The number of rows this inventory should have. a number between 1 - 6.
     */
    public abstract int getRows();

    /**
     * Called when the inventory gets closed.
     */
    public abstract void onClose();

    public boolean inventoryClicked(int slot, ItemStack stack, ClickType click) {
        ButtonClickEvent e = new ButtonClickEvent(this,stack,slot,click);
        if (buttons.get(slot) != null) {
            e.setCancelled(true);
            if (buttons.get(slot).getAction() != null) {
                buttons.get(slot).getAction().onClick(e);
                if (owner.getOpenInventory() == this) {
                    this.refresh();
                }
            }
        }
        return e.isCancelled();
    }

    public void refresh() {
        if (owner != null && owner.isOnline() && !(owner.handle.getOpenInventory().getTopInventory() instanceof CraftingInventory)) {
            buttons.clear();
            this.init();
            this.refreshing = true;
            owner.getNMS().openContainer(createInventory().getHandle());
            this.refreshing = false;
        } else {
            System.out.println("invalid inventory state!");
        }
    }

    public boolean interactionPerformed(int slot, ItemStack item, Entity entity, Block block, Action action) {
        ItemInteractEvent e = new ItemInteractEvent(owner,item,entity,block,action);
        if (buttons.get(slot) != null) {
            if (buttons.get(slot).getInteractAction() != null) {
                e.setCancelled(true);
                buttons.get(slot).getInteractAction().interact(e);
            }
        }
        if (e.getCancelMessage() != null && e.isCancelled()) {
            owner.sendMessage(e.getCancelMessage());
        }
        return e.isCancelled();
    }

    public ExtronPlayer getOwner() {
        return owner;
    }

    public void addButton(int slot, Material type, int count, int damage, String displayName, ItemLore lore, ClickAction action) {
        this.addButton(slot,new SimpleButton(slot,type).setCount(count).setDamage(damage).setDisplayName(displayName).setLore(lore).setAction(action));
    }

    /**
     * Creates a simple button and automatically adds it to the menu. When using this methods, DO NOT use {@link SimpleButton#addTo(InventoryMenu)}, or the button will be added twice.
     * @param slot the slot the button should be in
     * @param type the type of material.
     * @return a new {@link SimpleButton}, and adds it to the {@link #buttons}.
     */
    public SimpleButton createButton(int slot, Material type) {
        return new SimpleButton(slot,type).addTo(this);
    }

    public boolean addButton(int slot, Button button) {
        if (firstOpenSlot(slot) != -1) {
            this.buttons.put(firstOpenSlot(slot), button);
            return true;
        }
        return false;
    }

    public void setOwner(ExtronPlayer owner) {
        if (this.owner == null) {
            this.owner = owner;
        }
    }

    public Inventory createInventory() {
        Inventory inv = new CustomInventory(getRows()*9,getTitle());
        for (Map.Entry<Integer,Button> e : buttons.entrySet()) {
            inv.setItem(e.getKey(),e.getValue().createItem());
        }
        return inv;
    }

    public int firstOpenSlot(int from) {
        if (this.isEmpty()) {
            return from;
        }
        int i = from;
        while (true) {
            if (getButton(i) == null) {
                return i;
            }
            if (i >= MathUtils.clamp(this.getRows(),1,6) * 9) {
                return -1;
            }
            i++;
        }
    }

    public Button getButton(int i) {
        return buttons.get(i);
    }

    public boolean isEmpty() {
        return this.buttons.isEmpty();
    }

    public int getMiddleSlot() {
        switch (getRows()) {
            case 1:
            case 2:
                return 4;
            case 3:
            case 4:
                return 13;
            case 5:
            case 6:
                return 22;
                default:
                    return 0;
        }
    }

    public int countBorderSlots() {
        if (getRows() < 3) {
            return 4;
        }
        return 18 + ((getRows() - 2) * 2);
    }

    public void drawLine(int index, boolean vertical, Function<Integer,Button> buttonSupplier) {
        new ButtonSeparator(index,vertical,buttonSupplier).addTo(this);
    }

    public void addBackButton(Supplier<InventoryMenu> prev) {
        this.addBackButton((getRows() - 1)* 9,prev);
    }

    public void addBackButton(int slot, Supplier<InventoryMenu> prev) {
        this.addBackButton(slot,prev,null);
    }

    public void addBackButton(int slot, Supplier<InventoryMenu> prev, ItemLore lore) {
        this.addButton(slot,new ButtonBack(slot,prev,lore));
    }

    public void addCloseButton() {
        this.addCloseButton((getRows() - 1) + 4);
    }

    public void addCloseButton(int slot) {
        this.addButton(slot,new ButtonClose(slot));
    }

    public void throwEmptyError(int slot, String name, String msg) {
        this.addButton(slot, new EmptyErrorButton(name,msg));
    }

    public void throwError(int slot) {
        this.addButton(slot, new ErrorButton());
    }

    public void drawNumber(int number, Material type, int topLeftSlot) {
        this.drawNumber(number,type,0,topLeftSlot);
    }

    public void drawNumber(int number, Material type, int damage, int topLeftSlot) {
        new MenuNumberDrawing(number,type,damage,topLeftSlot).addTo(this);
    }

    public void drawRect(int topLeft, int width, int height, BiFunction<Integer,Integer,Button> buttonSupplier, boolean fill) {
        new RectDrawer(topLeft,width,height,buttonSupplier,fill).addTo(this);
    }
}
