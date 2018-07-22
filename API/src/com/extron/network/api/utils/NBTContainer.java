package com.extron.network.api.utils;

import net.minecraft.server.v1_8_R1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class NBTContainer {

    private NBTTagCompound tag;

    public NBTContainer() {
        this.tag = new NBTTagCompound();
    }

    public NBTContainer(NBTTagCompound tag) {
        this.tag = tag == null ? new NBTTagCompound() : tag;
    }

    public void setString(String key, String value) {
        this.set(key,value,NBTTagCompound::setString);
    }

    public void setInt(String key, int value) {
        this.set(key,value,NBTTagCompound::setInt);
    }

    public void setByte(String key, byte value) {
        this.set(key,value,NBTTagCompound::setByte);
    }

    public void setShort(String key, short value) {
        this.set(key,value,NBTTagCompound::setShort);
    }

    public void setLong(String key, long value) {
        this.set(key,value,NBTTagCompound::setLong);
    }

    public void setDouble(String key, double value) {
        this.set(key,value,NBTTagCompound::setDouble);
    }

    public void setFloat(String key, float value) {
        this.set(key,value,NBTTagCompound::setFloat);
    }

    public void setBoolean(String key, boolean value) {
        this.set(key,value,NBTTagCompound::setBoolean);
    }

    public <L> void setList(String key, List<L> value) {
        NBTTagList list = new NBTTagList();
        for (L v : value) {
            list.add(getNBTBase(v));
        }
        this.set(key,list,NBTTagCompound::set);
    }

    public void setList(String key, NBTTagList list) {
        this.set(key,list,NBTTagCompound::set);
    }

    public void setCompound(String s, NBTContainer c) {
        NBTTagCompound comp = c.tag;
        this.setBase(s,comp);
    }

    public void setBase(String key, NBTBase base) {
        this.set(key,base,NBTTagCompound::set);
    }

    public void setObject(String key, Object value) {
        this.setBase(key,getNBTBase(value));
    }

    private NBTBase getNBTBase(Object obj) {
        if (obj instanceof Integer) {
            return new NBTTagInt((int) obj);
        }
        if (obj instanceof String) {
            return new NBTTagString((String) obj);
        }
        if (obj instanceof Byte) {
            return new NBTTagByte((byte) obj);
        }
        if (obj instanceof Short) {
            return new NBTTagShort((short) obj);
        }
        if (obj instanceof Long) {
            return new NBTTagLong((long) obj);
        }
        if (obj instanceof Double) {
            return new NBTTagDouble((double) obj);
        }
        if (obj instanceof Float) {
            return new NBTTagFloat((float) obj);
        }
        if (obj instanceof Boolean) {
            return new NBTTagByte((byte) ((boolean)obj ? 1 : 0));
        }
        if (obj instanceof List) {
            NBTTagList nbtList = new NBTTagList();
            List list = (List)obj;
            for (Object o : list) {
                nbtList.add(getNBTBase(o));
            }
            return nbtList;
        }
        if (obj instanceof NBTTagCompound) {
            return (NBTTagCompound)obj;
        }
        if (obj instanceof NBTContainer) {
            return ((NBTContainer)obj).tag;
        }
        return new NBTTagString(String.valueOf(obj));
    }

    private <T> void set(String key, T value, CiConsumer<NBTTagCompound,String,T> setter) {
        int period = key.indexOf('.');
        if (period > 0) {
            String parent = key.substring(0, period);
            NBTContainer c = this.getSubContainer(parent);
            if (c == null) {
                c = new NBTContainer();
            }
            c.set(key.substring(period+1),value,setter);
            this.setCompound(parent,c);
        } else {
            setter.accept(tag,key.substring(period+1),value);
        }
    }

    public NBTContainer getSubContainer(String key) {
        NBTTagCompound c = this.getCompound(key);
        return c == null ? null : new NBTContainer(c);
    }

    public void setRawCompound(String key, String rawNBT) {
        if (rawNBT.startsWith("{") && rawNBT.endsWith("}")) {
            try {
                NBTContainer c = NBTContainer.parse(rawNBT);
                this.setCompound(key,c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static NBTContainer parse(String rawNBT) {
        return new NBTContainer(MojangsonParser.parse(rawNBT));
    }

    public NBTTagCompound getCompound(String key) {
        return get(key,NBTTagCompound::getCompound);
    }

    public String getString(String key) {
        return get(key,NBTTagCompound::getString);
    }

    public int getInt(String key) {
        return get(key,NBTTagCompound::getInt);
    }

    public byte getByte(String key) {
        return get(key,NBTTagCompound::getByte);
    }

    public short getShort(String key) {
        return get(key,NBTTagCompound::getShort);
    }

    public long getLong(String key) {
        return get(key,NBTTagCompound::getLong);
    }

    public double getDouble(String key) {
        return get(key,NBTTagCompound::getDouble);
    }

    public float getFloat(String key) {
        return get(key,NBTTagCompound::getFloat);
    }

    public boolean getBoolean(String key) {
        return get(key,NBTTagCompound::getBoolean);
    }

    public NBTTagList getList(String key, int type) {
        int period = key.indexOf('.');
        if (period > 0) {
            String parent = key.substring(0,period);
            NBTContainer c = this.getSubContainer(parent);
            if (c == null) return null;
            return c.getList(key.substring(period+1),type);
        } else {
            return tag.getList(key,type);
        }
    }

    public List<NBTContainer> getCompoundList(String key) {
        NBTTagList list = getList(key,10);
        List<NBTContainer> contList = new ArrayList<>();
        if (list == null) return null;
        for (int i = 0; i < list.size(); i++) {
            NBTTagCompound c = list.get(i);
            contList.add(new NBTContainer(c));
        }
        return contList;
    }

    private <T> T get(String key, BiFunction<NBTTagCompound,String,T> getter) {
        int period = key.indexOf('.');
        if (period > 0) {
            String parent = key.substring(0,period);
            NBTContainer c = this.getSubContainer(parent);
            if (c == null) return null;
            return c.get(key.substring(period+1),getter);
        } else {
            return getter.apply(tag,key);
        }
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag.toString();
    }
}
