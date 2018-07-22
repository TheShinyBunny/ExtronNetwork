package com.extron.network.api.inventory;

import org.bukkit.Material;

public class MenuNumberDrawing implements MenuContent {

    private final int number;
    private final Material item;
    private final int damage;
    private final int topLeftSlot;

    public MenuNumberDrawing(int number, Material item, int damage, int topLeftSlot) {
        this.number = number;
        this.item = item;
        this.damage = damage;
        this.topLeftSlot = topLeftSlot;
    }

    @Override
    public MenuNumberDrawing addTo(InventoryMenu gui) {
        if (number == 1) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 10));
            this.addButton(gui, createButton(topLeftSlot + 19));
            this.addButton(gui, createButton(topLeftSlot + 28));
            this.addButton(gui, createButton(topLeftSlot + 37));
        }
        if (number == 2) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 11));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 27));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
        }
        if (number == 3) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 11));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 29));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
        }
        if (number == 4) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 2));
            this.addButton(gui, createButton(topLeftSlot + 9));
            this.addButton(gui, createButton(topLeftSlot + 11));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 29));
            this.addButton(gui, createButton(topLeftSlot + 38));
        }
        if (number == 5) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 9));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 29));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
        }
        if (number == 6) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 9));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 27));
            this.addButton(gui, createButton(topLeftSlot + 29));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
        }
        if (number == 7) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 11));
            this.addButton(gui, createButton(topLeftSlot + 20));
            this.addButton(gui, createButton(topLeftSlot + 29));
            this.addButton(gui, createButton(topLeftSlot + 38));
        }
        if (number == 8) {
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot));
            this.addButton(gui, createButton(topLeftSlot + 9));
            this.addButton(gui, createButton(topLeftSlot + 11));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 18));
            this.addButton(gui, createButton(topLeftSlot + 27));
            this.addButton(gui, createButton(topLeftSlot + 29));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
            this.addButton(gui, createButton(topLeftSlot + 36));
        }
        return this;
    }

    private void addButton(InventoryMenu gui, SimpleButton button) {
        gui.addButton(button.getSlot(),button);
    }

    private SimpleButton createButton(int slot) {
        return new SimpleButton(slot,item)
                .setDamage(damage)
                .setDisplayName("");
    }

}
