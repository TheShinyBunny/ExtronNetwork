package com.extron.network.api.inventory.defaults;

import com.extron.network.api.inventory.*;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ProfileMenu extends InventoryMenu {

    private final ExtronPlayer profile;

    public ProfileMenu(ExtronPlayer profile) {
        this.profile = profile;
    }

    @Override
    public void init() {
        createButton(4, Material.SKULL_ITEM)
                .setSkullOwner(profile)
                .setDisplayName(profile.getDisplayName())
                .setLore(ItemLore.create()
                    .empty()
                    .parameter("Level",profile.getLevel())
                    .parameter("XP",profile.getXp())
                    .empty()
                    .moneyParameter("Coins",TextUtils.numberComma(profile.getCoins())));
        new ButtonSeparator(1,false,(i)->new EmptyButton(Material.THIN_GLASS)).addTo(this);
        createButton(18,Material.BOOK_AND_QUILL)
                .setDisplayName("Statistics")
                .setLore(ItemLore.create().empty().description("Click here to show the statistics of " + profile.getName()))
                .setAction(ClickAction.open(StatisticsMenu::new,profile));
        createButton(19,Material.PAPER)
                .setDisplayName("Activity")
                .setLore(ItemLore.create()
                        .empty()
                        .conditioned(profile.isOnline(),ChatColor.GREEN + "Online",ChatColor.GRAY + "" + ChatColor.ITALIC + "Last Online: " + profile.getLastOnline())
                        .parameter("Times Logged In",profile.getTimesLogin())
                        .line("First Login: " + ChatColor.BLUE + profile.getFirstLogin()));
    }

    @Override
    public String getTitle() {
        return profile.getName() + "'s Profile";
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void onClose() {

    }
}
