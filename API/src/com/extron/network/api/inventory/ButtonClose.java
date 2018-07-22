package com.extron.network.api.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ButtonClose extends ButtonBase {
    public ButtonClose(int slot) {
        super(slot);
    }

    @Override
    public Material getType() {
        return Material.BARRIER;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED + "Close";
    }

    @Override
    public ItemLore getLore() {
        return ItemLore.create()
                .empty()
                .description("Click to close this menu.");
    }

    @Override
    public ClickAction getAction() {
        return e -> e.getPlayer().closeInventory();
    }
}
