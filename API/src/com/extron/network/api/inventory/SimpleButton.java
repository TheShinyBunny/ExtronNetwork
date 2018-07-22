package com.extron.network.api.inventory;

import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SimpleButton extends ButtonBase {

    private int count, damage;
    private Material type;
    private String displayName;
    private ItemLore lore;
    private ClickAction action;
    private String skull;
    private InteractAction interact;

    public SimpleButton(int slot, Material type) {
        super(slot);
        this.type = type;
        this.count = 1;
    }

    public SimpleButton(Material type) {
        super();
        this.type = type;
        this.count = 1;
    }

    public SimpleButton setCount(int count) {
        this.count = count;
        return this;
    }

    public SimpleButton setDamage(int damage) {
        this.damage = damage;
        return this;
    }

    public SimpleButton setDisplayName(String name) {
        if (!name.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            name = ChatColor.RESET + name;
        }
        this.displayName = name;
        return this;
    }

    public SimpleButton setLore(ItemLore lore) {
        this.lore = lore;
        return this;
    }

    public SimpleButton setAction(ClickAction action) {
        this.action = action;
        return this;
    }

    public SimpleButton setInteraction(InteractAction action) {
        this.interact = action;
        return this;
    }

    public SimpleButton setSkullOwner(ExtronPlayer owner) {
        if (this.type == Material.SKULL_ITEM) {
            this.setDamage(3);
            this.skull = owner.getName();
        }
        return this;
    }

    public SimpleButton setSkullOwner(String name) {
        if (this.type == Material.SKULL_ITEM) {
            this.setDamage(3);
            this.skull = name;
        }
        return this;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ClickAction getAction() {
        return action;
    }

    @Override
    public InteractAction getInteractAction() {
        return interact;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public ItemLore getLore() {
        return lore;
    }

    @Override
    public Material getType() {
        return type;
    }


    @Override
    public SimpleButton addTo(InventoryMenu menu) {
        return (SimpleButton) super.addTo(menu);
    }

    @Override
    public ExtronStack createItem() {
        return super.createItem().setSkullOwner(skull);
    }
}
