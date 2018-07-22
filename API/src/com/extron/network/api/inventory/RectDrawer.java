package com.extron.network.api.inventory;

import java.util.function.BiFunction;

public class RectDrawer implements MenuContent {
    private final int topLeft;
    private final int width;
    private final BiFunction<Integer, Integer, Button> supply;
    private final int height;
    private final boolean fill;

    public RectDrawer(int topLeft, int width, int height, BiFunction<Integer,Integer,Button> buttonSupplier, boolean fill) {
        this.topLeft = topLeft;
        this.width = width;
        this.height = height;
        this.supply = buttonSupplier;
        this.fill = fill;
    }

    @Override
    public MenuContent addTo(InventoryMenu menu) {
        int i = 0;
        for (int x = topLeft; x < topLeft + height * 9; x+=9, i++) {
            for (int y = 0; y < width; y++) {
                if (menu.getButton(x + y) == null) {
                    if (fill || i == 0 || i == height - 1 || y == 0 || y == width - 1) {
                        menu.addButton(x + y,supply.apply(y,i));
                    }
                }
            }
        }
        return this;
    }
}
