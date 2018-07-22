package com.extron.network.api.inventory.defaults;

import com.extron.network.api.collection.Category;
import com.extron.network.api.inventory.Button;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.inventory.SmartGrid;

import java.util.Collection;
import java.util.List;

public abstract class CategoriesMenuBase<T extends Category> extends InventoryMenu {

    public abstract List<T> getCategories();

    public abstract Button createButtonFor(T category);

    @Override
    public void init() {
        SmartGrid grid = new SmartGrid();
        for (T c : getCategories()) {
            grid.addButton(createButtonFor(c));
        }
        grid.addTo(this);
    }

    @Override
    public int getRows() {
        return 6;
    }
}
