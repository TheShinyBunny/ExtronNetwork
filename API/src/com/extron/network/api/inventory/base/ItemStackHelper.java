package com.extron.network.api.inventory.base;

import com.extron.network.api.utils.NBTContainer;
import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.Item;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public class ItemStackHelper {
    
    private static final Map<PotionType,PotionData> potionTypeMap = new HashMap<>();

    static {
        potionTypeMap.put(PotionType.REGEN,new PotionData(8193,8225,8257,16385,16417,16449));
        potionTypeMap.put(PotionType.SPEED,new PotionData(8194,8226,8258,16386,16418,16450));
        potionTypeMap.put(PotionType.FIRE_RESISTANCE,new PotionData(8227,-1,8259,16419,-1,16451));
        potionTypeMap.put(PotionType.POISON,new PotionData(8196,8228,8260,16388,16420,16452));
        potionTypeMap.put(PotionType.INSTANT_HEAL,new PotionData(8261,8229,-1,16453,16421,-1));
        potionTypeMap.put(PotionType.NIGHT_VISION,new PotionData(8230,-1,8262,16422,-1,16454));
        potionTypeMap.put(PotionType.WEAKNESS,new PotionData(8232,-1,8264,16424,-1,16456));
        potionTypeMap.put(PotionType.STRENGTH,new PotionData(8201,8233,8265,16393,16425,16457));
        potionTypeMap.put(PotionType.SLOWNESS,new PotionData(8234,-1,8266,16426,-1,16458));
        potionTypeMap.put(PotionType.JUMP,new PotionData(8203,8235,8267,16395,16427,16459));
        potionTypeMap.put(PotionType.INSTANT_DAMAGE,new PotionData(8268,8236,-1,16460,16428,-1));
        potionTypeMap.put(PotionType.WATER_BREATHING,new PotionData(8237,-1,8269,16429,-1,16461));
        potionTypeMap.put(PotionType.INVISIBILITY,new PotionData(8238,-1,8270,16430,-1,16462));
    }
    
    public static ExtronStack fromString(String s) {
        return fromString(s,null);
    }

    public static ExtronStack fromString(String s, ExtronStack def) {
        NBTContainer nbt;
        try {
            nbt = NBTContainer.parse(s);
        } catch (Exception e) {
            return def;
        }
        return fromNBT(nbt, def);
    }

    public static ExtronStack fromNBT(NBTContainer nbt, ExtronStack def) {
        String id = nbt.getString("id");
        if (id == null) return def;
        int count = nbt.getByte("Count");
        if (count == 0) return def;
        short damage = nbt.getShort("Damage");
        NBTContainer tag = nbt.getSubContainer("tag");
        Material m = getMaterialById(id);
        if (m == null) return def;
        ExtronStack stack = new ExtronStack(m,count,damage);
        if (tag != null) {
            stack.setNBT(tag);
        }
        return stack;
    }

    public static Material getMaterialById(String id) {
        Item i = Item.d(id);
        if (i == null) return null;
        return CraftMagicNumbers.getMaterial(i);
    }

    public static ExtronStack createPotion(PotionType type) {
        return createPotion(type,false,false,false);
    }

    public static ExtronStack createPotion(PotionType type, boolean strong, boolean longer, boolean splash) {
        int data = getPotionTypeData(type,strong,longer,splash);
        if (data == -1) {
            return new ExtronStack(Material.POTION);
        }
        return new ExtronStack(Material.POTION,1, data);
    }

    private static int getPotionTypeData(PotionType type, boolean strong, boolean longer, boolean splash) {
        PotionData data = potionTypeMap.get(type);
        if (data == null) return -1;
        if (strong) {
            if (splash) {
                return data.splashStrong;
            } else {
                return data.strongData;
            }
        }
        if (longer) {
            if (splash) {
                return data.splashLong;
            } else {
                return data.longData;
            }
        }
        if (splash) {
            return data.splashDefault;
        }
        return data.defaultData;
    }

    public static int getFirstFreeSlot(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

    public static ItemMeta createMetaFromNBT(NBTContainer nbt, Material type) {
        return null;
    }

    public static Item getItemOfMaterial(Material type) {
        return CraftMagicNumbers.getItem(type);
    }

    public static Material getMaterialOfItem(Item item) {
        return CraftMagicNumbers.getMaterial(item);
    }

    public static net.minecraft.server.v1_8_R1.ItemStack copyNMSItem(net.minecraft.server.v1_8_R1.ItemStack stack, int count) {
        net.minecraft.server.v1_8_R1.ItemStack stack1 = stack.cloneItemStack();
        stack1.count = count;
        return stack1;
    }

    private static class PotionData {

        public int defaultData;
        public int strongData;
        public int longData;
        public int splashDefault;
        public int splashLong;
        public int splashStrong;

        public PotionData(int defaultData, int strongData, int longData, int splashDefault, int splashStrong, int splashLong) {
            this.defaultData = defaultData;
            this.strongData = strongData;
            this.longData = longData;
            this.splashDefault = splashDefault;
            this.splashLong = splashLong;
            this.splashStrong = splashStrong;
        }
    }
}
