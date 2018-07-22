package com.extron.network.api.inventory;

import com.extron.network.api.collection.CategoryItem;
import com.extron.network.api.inventory.base.ItemDisplayable;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ItemLore {

	private List<String> theLore = new ArrayList<>();

	public static ItemLore create() {
		return new ItemLore();
	}

    public ItemLore description(ChatColor color, String desc) {
		this.theLore.addAll(ListUtils.modifyAll(TextUtils.autoNewLine(desc, 25),s->color + s));
		return this;
	}

	public ItemLore description(String desc) {
		return this.description(ChatColor.GRAY,desc);
	}

	public static ItemLore description(ItemDisplayable item) {
		return new ItemLore().empty().description(item.getDescription());
	}

	public ItemLore line(String s) {
		this.theLore.add(ChatColor.RESET + s);
		return this;
	}
	
	public ItemLore conditioned(boolean cond, String line) {
		if (cond) this.theLore.add(ChatColor.RESET + line);
		return this;
	}
	public ItemLore conditioned(boolean cond, String line, String ifelse) {
		if (cond) this.theLore.add(ChatColor.RESET + line);
		else this.theLore.add(ChatColor.RESET + ifelse);
		return this;
	}
	
	public ItemLore category(String s) {
		this.theLore.add(ChatColor.DARK_GRAY + s);
		return this;
	}
	
	public ItemLore parameter(String pname, int i) {
		this.theLore.add(ChatColor.RESET + pname + ": " + ChatColor.AQUA + i);
		return this;
	}
	
	public ItemLore moneyParameter(String pname, String amount) {
		this.theLore.add(ChatColor.RESET + pname + ": " + ChatColor.GOLD + amount);
		return this;
	}
	
	public ItemLore empty() {
		this.theLore.add("");
		return this;
	}
	
	public ItemLore rightClickTo (String action) {
		this.theLore.add(ChatColor.LIGHT_PURPLE + "Right Click to " + action + "!");
		return this;
	}
	
	public ItemLore leftClickTo (String action) {
		this.theLore.add(ChatColor.YELLOW + "Left Click to " + action + "!");
		return this;
	}
	
	public ItemLore clickTo (String action) {
		this.theLore.add(ChatColor.YELLOW + "Click to " + action + "!");
		return this;
	}
	
	public ItemLore error (String error) {
		this.theLore.add(ChatColor.RED + error);
		return this;
	}
	
	public List<String> build() {
		return this.theLore;
	}

	public void clear() {
		this.theLore.clear();
	}

	public ItemLore found(int size, int total) {
		ChatColor color = size < total / 2 ? ChatColor.RED : size < total ? ChatColor.YELLOW : ChatColor.GREEN;
		return this.line("Found: " + color + TextUtils.numberComma(size) + ChatColor.GRAY + "/" + ChatColor.GREEN + TextUtils.numberComma(total));
	}
}
