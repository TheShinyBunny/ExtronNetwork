package com.extron.network.api.inventory.defaults;

import com.extron.network.api.Main;
import com.extron.network.api.game.Game;
import com.extron.network.api.inventory.ButtonBack;
import com.extron.network.api.inventory.InventoryMenu;
import com.extron.network.api.inventory.ItemLore;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.Material;

public class StatisticsMenu extends InventoryMenu {

    private final ExtronPlayer profile;

    public StatisticsMenu(ExtronPlayer profile) {
        this.profile = profile;
    }

    @Override
    public void init() {
        createButton(4, Material.GRASS)
                .setDisplayName("General Stats")
                .setLore(profile.getStatistics().createDisplayLore(null));
        for (Game g : Main.getGames()) {
            createButton(9,g.getIcon())
                    .setDamage(g.getIconDamage())
                    .setDisplayName(g.getGameName())
                    .setLore(profile.getStatistics().createDisplayLore(g));
        }
        new ButtonBack(45,()->new ProfileMenu(profile),ItemLore.create().empty().description("Click here to go back to " + profile.getName() + "'s profile page")).addTo(this);
    }

    @Override
    public String getTitle() {
        return "Statistics of " + profile.getName();
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void onClose() {

    }
}
