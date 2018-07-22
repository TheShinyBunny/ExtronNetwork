package com.extron.network.api.inventory.base;

import com.extron.network.api.Main;
import com.extron.network.api.inventory.ItemLore;
import com.extron.network.api.inventory.interactions.StackInteractAction;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.NBTContainer;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftMetaBanner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ExtronStack {

    private Material type;
    private int count;
    private int data;

    private NBTContainer nbt;
    private StackInteractAction interactAction;

    public ExtronStack(Material type) {
        this(type,1);
    }

    public ExtronStack(Material type, int count) {
        this(type,count,0);
    }

    public ExtronStack(Material type, int count, int data) {
        this(type, count, data,null);
    }

    public ExtronStack(Material type, int count, int data, NBTContainer nbt) {
        this.type = type;
        this.count = count;
        this.data = data;
        this.nbt = nbt;
    }

    public ExtronStack(net.minecraft.server.v1_8_R1.ItemStack stack) {
        this(ItemStackHelper.getMaterialOfItem(stack.getItem()),stack.count,stack.getData(),stack.getTag() == null ? null : new NBTContainer(stack.getTag()));
    }

    public void createNBT() {
        if (nbt == null) {
            nbt = new NBTContainer();
        }
    }

    public ItemStack toBukkitStack() {
        ItemStack stack = new ItemStack(type,count, (short) data);
        if (nbt != null) {
            //stack.setItemMeta(ItemStackHelper.createMetaFromNBT(nbt,type));
            Bukkit.getUnsafe().modifyItemStack(stack,nbt.toString());
        }

        return stack;
    }

    public net.minecraft.server.v1_8_R1.ItemStack toNMSItem() {
        net.minecraft.server.v1_8_R1.ItemStack stack = new net.minecraft.server.v1_8_R1.ItemStack(ItemStackHelper.getItemOfMaterial(type),count,data);
        if (nbt != null) {
            stack.setTag(nbt.getTag());
        }
        return stack;
    }

    public Material getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public int getData() {
        return data;
    }

    public NBTContainer getNBT() {
        return nbt;
    }

    public boolean hasNBT() {
        return nbt != null;
    }

    public ExtronStack setCount(int count) {
        this.count = count;
        return this;
    }

    public ExtronStack setDamage(int damage) {
        this.data = damage;
        return this;
    }

    public ExtronStack setInteractAction(StackInteractAction a) {
        createNBT();
        this.nbt.setCompound("InteractAction",a.toNBT());
        this.interactAction = a;
        return this;
    }

    public ExtronStack setLore(ItemLore lore) {
        createNBT();
        this.nbt.setList("display.Lore",lore.build());
        return this;
    }

    public ExtronStack setDisplayName(String displayName) {
        createNBT();
        this.nbt.setString("display.Name",ChatColor.RESET + displayName);
        return this;
    }

    public StackInteractAction getInteractAction() {
        if (interactAction != null) return interactAction;
        if (nbt == null) return null;
        NBTContainer c = nbt.getSubContainer("InteractAction");
        if (c == null) return null;
        String id = c.getString("id");
        if (id == null) return null;
        StackInteractAction a = ListUtils.firstMatch(Main.getInteractActions(),s->s.getId().equalsIgnoreCase(id));
        if (a == null) return null;
        return a.load(c);
    }

    public ExtronStack setSkullOwner(ExtronPlayer owner) {
        if (owner == null) return this;
        return setSkullOwner(owner.getName());
    }

    public ExtronStack setSkullOwner(String owner) {
        if (owner == null) return this;
        this.type = Material.SKULL_ITEM;
        this.data = 3;
        createNBT();
        nbt.setString("SkullOwner",owner);
        return this;
    }

    public ExtronStack addEnchantment(Enchantment enchantment, int lvl) {
        createNBT();
        List<NBTContainer> enchs = nbt.getCompoundList("ench");
        NBTContainer c = new NBTContainer();
        c.setShort("id", (short) enchantment.getId());
        c.setShort("lvl", (short) lvl);
        enchs.add(c);
        nbt.setList("ench",enchs);
        return this;
    }

    public ExtronStack setNBT(NBTContainer tag) {
        this.nbt = tag;
        return this;
    }

    @Override
    public String toString() {
        return this.toNMSItem().save(new NBTTagCompound()).toString();
    }

}
