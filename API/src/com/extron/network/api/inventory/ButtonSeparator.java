package com.extron.network.api.inventory;

import java.util.function.Function;

public class ButtonSeparator implements MenuContent {

    private final int index;
    private final boolean vertical;
    private final Function<Integer, Button> supply;

    public ButtonSeparator(int index, boolean vertical, Function<Integer,Button> buttonSupplier) {
        this.index = index;
        this.vertical = vertical;
        this.supply = buttonSupplier;
    }

    @Override
    public MenuContent addTo(InventoryMenu menu) {
        for (int i = 0; i < (vertical ? menu.getRows() : 9); i++) {
            int slot;
            if (vertical) {
                slot = i * 9 + index;
            } else {
                slot = index * 9 + i;
            }
            menu.addButton(slot,supply.apply(i));
        }
        return this;
    }
}
