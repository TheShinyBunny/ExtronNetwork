package com.extron.network.api.inventory;

public class SmartGrid extends ButtonGrid {
    public SmartGrid() {
        super(0, 9);
    }

    @Override
    public MenuContent addTo(InventoryMenu menu) {
        if (buttons.isEmpty()) {
            return this;
        } else if (buttons.size() == 1) {
            menu.addButton(menu.getMiddleSlot(),buttons.get(0));
            return this;
        } else if (buttons.size() < 9) {
            switch (buttons.size()) {
                case 2:
                    topLeft = menu.getMiddleSlot() - 2;
                    horizontal = 4;
                    cols = 2;
                    break;
                case 3:
                    topLeft = menu.getMiddleSlot() - 2;
                    horizontal = 2;
                    cols = 3;
                    break;
                case 4:
                    topLeft = menu.getMiddleSlot() - 3;
                    horizontal = 2;
                    cols = 4;
                    break;
                case 5:
                case 6:
                    topLeft = 10;
                    horizontal = 3;
                    vertical = 2;
                    cols = 3;
                    break;
                case 7:
                    if (menu.getRows() > 3) {
                        topLeft = 10;
                        horizontal = 2;
                        vertical = 2;
                        cols = 4;
                    } else {
                        topLeft = menu.getMiddleSlot() - 3;
                        horizontal = 1;
                        cols = 7;
                    }
                    break;
                case 8:
                    if (menu.getRows() > 3) {
                        topLeft = 10;
                        horizontal = 2;
                        vertical = 2;
                        cols = 4;
                    } else {
                        topLeft = menu.getMiddleSlot() - 4;
                        horizontal = 1;
                        cols = 8;
                    }
                    break;
            }
        } else if (buttons.size() < menu.getRows() * 9 - menu.countBorderSlots()) {
            this.cols = 7;
            this.topLeft = 10;
        } else {
            this.cols = 9;
            this.topLeft = 0;
        }
        return super.addTo(menu);
    }
}
