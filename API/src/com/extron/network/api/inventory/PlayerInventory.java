package com.extron.network.api.inventory;

import com.extron.network.api.collection.Gadget;
import com.extron.network.api.event.inventory.ButtonClickEvent;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.inventory.base.Inventory;
import com.extron.network.api.inventory.defaults.CollectibleTypesMenu;
import com.extron.network.api.inventory.defaults.MainMenu;
import com.extron.network.api.inventory.defaults.ProfileMenu;
import com.extron.network.api.inventory.interactions.MenuOpenerInteract;
import com.extron.network.api.inventory.interactions.StackInteractAction;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class PlayerInventory extends Inventory {

    private final ExtronPlayer owner;
    private Map<Integer, Button> buttons;

    public PlayerInventory(ExtronPlayer p) {
        super(((CraftInventoryPlayer)p.handle.getInventory()).getInventory());
        owner = p;
        buttons = new HashMap<>();
    }

    public void init() {
        if (owner.getWorld().isLobby()) {
            createButton(0, Material.COMPASS)
                    .setDisplayName("Main Menu")
                    .setLore(ItemLore.create().description("Right Click to open the server's Main Menu."))
                    .setInteraction(InteractAction.open(MainMenu::new));
            createButton(1, Material.SKULL_ITEM)
                    .setSkullOwner(owner)
                    .setDisplayName("My Profile")
                    .setLore(ItemLore.create().description("Right Click to open your profile menu."))
                    .setInteraction(InteractAction.open(ProfileMenu::new));
            createButton(2, Material.ENDER_CHEST)
                    .setDisplayName("Collectibles")
                    .setLore(ItemLore.create().description("Right Click to open your collectibles menu."))
                    .setInteraction(InteractAction.open(CollectibleTypesMenu::new));
            if (owner.getSelectedGadget() != null) {
                Gadget g = owner.getSelectedGadget();
                createButton(8, g.getIcon())
                        .setDisplayName(g.getDisplayName())
                        .setLore(ItemLore.create().description("Right Click to activate the " + g.getDisplayName() + " gadget!"))
                        .setDamage(g.getIconDamage())
                        .setInteraction(g::onClick);
            }

            if (owner.isOnline()) {
                for (Map.Entry<Integer, Button> e : buttons.entrySet()) {
                    this.setItem(e.getKey(), e.getValue().createItem());
                }
            }
        }
    }

    public SimpleButton createButton(int slot, Material type) {
        SimpleButton b = new SimpleButton(slot,type);
        this.buttons.put(slot,b);
        return b;
    }

    @Override
    public String getTitle() {
        return "Inventory";
    }

    public ExtronStack getHeldItem() {
        return getItem(owner.getSelectedSlot());
    }

    public boolean interactWithItem(int slot, ItemStack item, Entity entity, Block block, Action action) {
        if (slot > 8) {
            return false;
        }
        ItemInteractEvent e = new ItemInteractEvent(owner,item,entity,block,action);
        if (getItem(slot) != null) {
            if (buttons.get(slot) != null && buttons.get(slot).getInteractAction() != null) {
                e.setCancelled(true);
                buttons.get(slot).getInteractAction().interact(e);
            } else {
                if (getItem(slot).getInteractAction() != null) {
                    e.setCancelled(true);
                    getItem(slot).getInteractAction().onClick(e);
                }
            }
        }
        if (e.getCancelMessage() != null && e.isCancelled()) {
            owner.sendMessage(e.getCancelMessage());
        }
        return e.isCancelled();
    }

    @Override
    public void clear() {
        super.clear();
        buttons.clear();
        if (owner.isOnline()) {
            owner.handle.getInventory().clear();
        }
    }

    public boolean inventoryClicked(int slot, ItemStack item, ClickType click) {
        if (buttons.get(slot) == null) {
            ItemInteractEvent e = new ItemInteractEvent(owner, item, null, null, Action.PHYSICAL);
            if (getItem(slot) != null) {
                if (getItem(slot).getInteractAction() != null) {
                    getItem(slot).getInteractAction().onClick(e);
                }
            }
            if (e.getCancelMessage() != null && e.isCancelled()) {
                owner.sendMessage(e.getCancelMessage());
            }
            return e.isCancelled();
        } else {
            ButtonClickEvent e = new ButtonClickEvent(null,item,slot,click);
            if (buttons.get(slot).getAction() != null) {
                e.setCancelled(true);
                buttons.get(slot).getAction().onClick(e);
            }
            return e.isCancelled();
        }
    }

    public void addButton(Button button) {
        if (this.getFirstOpenSlot(0) != -1) {
            this.buttons.put(getFirstOpenSlot(0),button);
            this.setItem(getFirstOpenSlot(0),button.createItem());
        }
    }

    public Button getHeldButton() {
        return buttons.get(owner.getSelectedSlot());
    }
}
