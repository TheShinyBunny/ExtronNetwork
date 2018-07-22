package com.extron.network.api.inventory;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ButtonGrid implements MenuContent {

    protected int cols;
    protected int topLeft;
    protected int vertical;
    protected int horizontal;
    protected List<Button> buttons;
    private String emptyName;
    private String emptyMsg;

    public ButtonGrid(int topLeftIndex, int cols, int verticalSpace, int horizontalSpace) {
        this.topLeft = topLeftIndex;
        this.cols = cols;
        this.vertical = verticalSpace;
        this.horizontal = horizontalSpace;
        this.buttons = new ArrayList<>();
    }

    public ButtonGrid(int topLeftIndex, int cols, int spacing) {
        this(topLeftIndex,cols,spacing,spacing);
    }

    public ButtonGrid(int topLeftIndex, int width) {
        this(topLeftIndex,width,1,1);
    }

    public SimpleButton createButton(Material type) {
        SimpleButton button = new SimpleButton(type);
        buttons.add(button);
        return button;
    }

    public void addButton(Button b) {
        this.buttons.add(b);
    }

    @Override
    public MenuContent addTo(InventoryMenu menu) {
        if (buttons.isEmpty()) {
            if (emptyMsg != null) {
                menu.addButton(menu.getMiddleSlot(),new EmptyErrorButton(emptyName,emptyMsg));
            }
            return this;
        }
        int x = topLeft % 9;
        int y = topLeft / 9;
        for (int i = 0; i < buttons.size(); i++) {
            Button b = buttons.get(i);
            menu.addButton(y * 9 + x, b);
            x += horizontal;
            if (x >= cols * horizontal + topLeft % 9) {
                y += vertical;
                x = topLeft % 9;
            }
        }
        return this;
    }

    public void setEmptyMessage(String name, String msg) {
        this.emptyName = name;
        this.emptyMsg = msg;
    }

    public boolean isEmpty() {
        return buttons.isEmpty();
    }
}
