package com.extron.network.api.inventory.defaults;

import com.extron.network.api.collection.Category;
import com.extron.network.api.collection.CategoryItem;
import com.extron.network.api.inventory.*;
import com.extron.network.api.utils.ListUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public abstract class SingleCategoryMenu<T extends Category> extends InventoryMenu {

    protected final T category;
    protected final Supplier<InventoryMenu> back;

    public SingleCategoryMenu(T category, Supplier<InventoryMenu> back) {
        this.category = category;
        this.back = back;
    }

    @Override
    public void init() {
        ButtonGrid grid = useSmartGrid() ? new SmartGrid() : new ButtonGrid(10,7);
        List<? extends CategoryItem> list = category.getAll();
        for (CategoryItem i : list) {
            if (i instanceof Category && ((Category) i).isMainCategory()) {
                Button b = createSubCategoryButton((Category) i);
                grid.addButton(b);
            } else if (i.getCategory().equals(category)) {
                Button b = createItemButton(i);
                grid.addButton(b);
            }
        }
        grid.addTo(this);
        new ButtonBack(getRows() * 9 - 9,back,ItemLore.create().description("Click to go back to the " + category.getParentName() + " menu")).addTo(this);
    }

    protected abstract Button createSubCategoryButton(Category category);

    protected abstract boolean useSmartGrid();

    protected abstract Button createItemButton(CategoryItem item);


}
