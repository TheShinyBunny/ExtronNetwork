package com.extron.network.api.collection.impl;

import com.extron.network.api.Main;
import com.extron.network.api.collection.Gadget;
import com.extron.network.api.collection.Rarity;
import com.extron.network.api.event.inventory.ItemInteractEvent;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.List;

public class LuckCookie implements Gadget {
    @Override
    public void onActivate(ExtronPlayer p) {
        List<String> list = Main.getMainConfig().getStringList("cookie_sentences");
        if (list == null || list.isEmpty()) {
            p.sendMessage(ChatColor.GOLD + "Sometimes you expect to get a strong sentence from a cookie, but then there's a bug in the code and you get the default one.");
            return;
        }
        String s = ListUtils.randomItem(list);
        p.sendMessage(ChatColor.GOLD + s);
    }

    @Override
    public void onActivateAt(ExtronPlayer p, Entity target) {

    }

    @Override
    public boolean foundInBasicLoot() {
        return true;
    }

    @Override
    public void shouldActivate(ItemInteractEvent e) {

    }

    @Override
    public int getCooldown() {
        return 3;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public String getId() {
        return "luck_cookie";
    }

    @Override
    public String getDisplayName() {
        return "Fortune Cookie";
    }

    @Override
    public Material getIcon() {
        return Material.COOKIE;
    }

    @Override
    public int getIconDamage() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "A cookie that will tell you some very important life tips";
    }
}
