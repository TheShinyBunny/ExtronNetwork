package com.extron.network.api.collection;

import com.extron.network.api.inventory.base.ItemDisplayable;

import java.util.List;

public interface Category extends ItemDisplayable {

    String getName();

    List<? extends CategoryItem> getAll();

    String getParentName();

    boolean isMainCategory();

}
