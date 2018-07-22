package com.extron.network.api.inventory.interactions;

import com.extron.network.api.Main;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.utils.NBTContainer;

public abstract class StackInteractAction {

    private final String id;

    public StackInteractAction(String id) {
        this.id = id;
        Main.registerInteractAction(this);
    }

    public String getId() {
        return id;
    }

    public abstract void onClick(ItemInteractEvent e);

    public NBTContainer toNBT() {
        NBTContainer c = new NBTContainer();
        c.setString("id",id);
        return c;
    }

    public StackInteractAction load(NBTContainer c) {
        return this;
    }
}
