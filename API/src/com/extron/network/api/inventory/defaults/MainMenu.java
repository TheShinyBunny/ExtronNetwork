package com.extron.network.api.inventory.defaults;

import com.extron.network.api.Main;
import com.extron.network.api.game.Game;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.inventory.SmartGrid;

public class MainMenu extends InventoryMenu {
    @Override
    public void init() {
        SmartGrid grid = new SmartGrid();
        for (Game g : Main.getGames()) {
            grid.addButton(g.createMenuButton());
        }
        grid.addTo(this);
    }

    @Override
    public String getTitle() {
        return "Main Menu";
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void onClose() {

    }
}
