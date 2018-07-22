package com.extron.network.api.inventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class Book extends ButtonBase {

    private String title;
    private String author;
    private List<String> pages;
    private ItemLore lore;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.pages = new ArrayList<>();
    }


    @Override
    public Material getType() {
        return Material.WRITTEN_BOOK;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public String getDisplayName() {
        return title;
    }

    @Override
    public ItemLore getLore() {
        return lore;
    }

    public Book setLore(ItemLore lore) {
        this.lore = lore;
        return this;
    }

    @Override
    public ClickAction getAction() {
        return null;
    }

    public net.minecraft.server.v1_8_R1.ItemStack toNMSItem() {
        return CraftItemStack.asNMSCopy(toBukkitItem());
    }

    public ItemStack toBukkitItem() {
        ItemStack is = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta m = (BookMeta) is.getItemMeta();
        m.setTitle(title);
        m.setAuthor(author);
        m.setPages(pages);
        is.setItemMeta(m);
        return is;
    }
}
