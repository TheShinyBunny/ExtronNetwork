package com.extron.network.api.config;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.Main;
import com.extron.network.api.inventory.base.ExtronStack;
import com.extron.network.api.inventory.base.ItemStackHelper;
import com.extron.network.api.utils.DataObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class ConfigSection implements ConfigurationSection, DataObject {

    private Map<String,Object> map;
    private ConfigSection parent;
    private String id;

    public ConfigSection(String id, ConfigSection parent) {
        this.map = new HashMap<>();
        this.parent = parent;
        this.id = id;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (!deep) return map.keySet();
        Set<String> result = new LinkedHashSet<>();
        this.mapChildrenKeys(result);
        return result;
    }

    private void mapChildrenKeys(Set<String> output) {
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            output.add(entry.getKey());
            if (entry.getValue() instanceof ConfigSection) {
                ((ConfigSection)entry.getValue()).mapChildrenKeys(output);
            }
        }
    }

    private static String createPath(ConfigSection section, String key) {
        char separator = '.';
        StringBuilder builder = new StringBuilder();
        if (section.parent != null) {
            for (ConfigSection parent = section; parent != null; parent = parent.parent) {
                if (builder.length() > 0) {
                    builder.insert(0, separator);
                }
                builder.insert(0, parent.getName());
            }
        }
        if (key != null && key.length() > 0) {
            if (builder.length() > 0) {
                builder.append(separator);
            }
            builder.append(key);
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        Map<String, Object> result = new LinkedHashMap<>();
        this.mapChildrenValues(result, deep);
        return result;
    }

    private void mapChildrenValues(Map<String, Object> output, boolean deep) {
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            output.put(createPath(this, entry.getKey()), entry.getValue());
            if (entry.getValue() instanceof ConfigSection && deep) {
                ((ConfigSection)entry.getValue()).mapChildrenValues(output, true);
            }
        }
    }

    @Override
    public boolean contains(String s) {
        return this.get(s) != null;
    }

    @Deprecated
    @Override
    public boolean isSet(String s) {
        return false;
    }

    @Deprecated
    @Override
    public String getCurrentPath() {
        return null;
    }

    @Override
    public String getName() {
        return id;
    }

    @Deprecated
    @Override
    public Configuration getRoot() {
        return null;
    }

    @Deprecated
    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    public ConfigSection parent() {
        return parent;
    }

    @Override
    public Object get(String s) {
        return this.get(s,null);
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path cannot be null");
        if (path.length() == 0) {
            return this;
        } else {
            int periodIndex = path.indexOf(".");
            if (periodIndex == -1) {
                Object o = map.get(path);
                if (o == null) {
                    return def;
                }
                return o;
            } else {
                String s = path.substring(0,periodIndex);
                String subPath = path.substring(periodIndex+1);
                Object section = map.get(s);
                if (section instanceof ConfigSection) {
                    return ((ConfigSection) section).get(subPath,def);
                } else {
                    return def;
                }
            }
        }
    }

    @Override
    public void set(String path, Object value) {
        Validate.notEmpty(path, "Cannot set to an empty path");
        if (value != null) {
            value = Config.toString(value);
        }
        int periodIndex = path.indexOf(".");
        if (periodIndex == -1) {
            if (value == null) {
                this.map.remove(path);
            } else {
                this.map.put(path,value);
            }
        } else {
            String s = path.substring(0,periodIndex);
            String subPath = path.substring(periodIndex+1);
            Object section = map.get(s);
            if (section instanceof ConfigSection) {
                ((ConfigSection) section).set(subPath,value);
            } else {
                ConfigSection newSection = new ConfigSection(s,this);
                newSection.set(subPath,value);
                map.put(s,newSection);
            }
        }
    }

    @Deprecated
    @Override
    public ConfigurationSection createSection(String s) {
        return null;
    }

    @Deprecated
    @Override
    public ConfigurationSection createSection(String s, Map<?, ?> map) {
        return null;
    }

    @Override
    public String getString(String s) {
        return this.getString(s,null);
    }

    @Override
    public String getString(String s, String s1) {
        Object o = this.get(s,s1);
        return o == null ? s1 : o.toString();
    }

    @Deprecated
    @Override
    public boolean isString(String s) {
        return true;
    }

    @Override
    public int getInt(String s) {
        return this.getInt(s,0);
    }

    @Override
    public int getInt(String s, int def) {
        Object o = this.get(s,def);
        return o == null ? def : o instanceof Integer ? (int)o : parseInt(o,def);
    }

    private int parseInt(Object o, int i) {
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return i;
        }
    }

    @Override
    public boolean isInt(String s) {
        Object o = this.get(s);
        if (o == null) return false;
        if (o instanceof Integer) return true;
        try {
            Integer.parseInt(o.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean getBoolean(String s) {
        return this.getBoolean(s,false);
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        Object o = this.get(s,b);
        return o == null ? b : o instanceof Boolean ? (boolean) o : Boolean.parseBoolean(o.toString());
    }

    @Override
    public boolean isBoolean(String s) {
        Object o = this.get(s);
        return o != null && (o instanceof Boolean || o.toString().equalsIgnoreCase("true"));
    }

    @Override
    public double getDouble(String s) {
        return this.getDouble(s,0);
    }

    @Override
    public double getDouble(String s, double v) {
        Object o = this.get(s,v);
        return o == null ? v : o instanceof Double ? (double)o : parseDouble(o,v);
    }

    private double parseDouble(Object o, double v) {
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return v;
        }
    }

    @Override
    public boolean isDouble(String s) {
        Object o = this.get(s);
        if (o == null) return false;
        if (o instanceof Double) return true;
        try {
            Double.parseDouble(o.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getLong(String s) {
        return this.getLong(s,0);
    }

    @Override
    public long getLong(String s, long l) {
        Object o = this.get(s,l);
        return o == null ? l : o instanceof Long ? (long)o : parseLong(o,l);
    }

    private long parseLong(Object o, long l) {
        try {
            return Long.parseLong(o.toString());
        } catch (Exception e) {
            return l;
        }
    }

    @Override
    public boolean isLong(String s) {
        Object o = this.get(s);
        if (o == null) return false;
        if (o instanceof Long) return true;
        try {
            Long.parseLong(o.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<?> getList(String s) {
        return this.getList(s,null);
    }

    @Override
    public List<?> getList(String path, List<?> def) {
        Object val = this.get(path, def);
        return (List)(val instanceof List ? val : def);
    }


    @Override
    public boolean isList(String s) {
        return get(s) instanceof List;
    }

    @Override
    public List<String> getStringList(String s) {
        List<?> list = this.getList(s);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<String> result = new ArrayList<>();
            Iterator var4 = list.iterator();

            while(true) {
                Object object;
                do {
                    if (!var4.hasNext()) {
                        return result;
                    }
                    object = var4.next();
                } while(!(object instanceof String) && !this.isPrimitiveWrapper(object));

                result.add(String.valueOf(object));
            }
        }
    }

    private boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }

    @Override
    public List<Integer> getIntegerList(String s) {
        List<?> list = this.getList(s);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Integer> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Integer) {
                    result.add((Integer) object);
                } else if (object instanceof String) {
                    try {
                        result.add(Integer.valueOf((String) object));
                    } catch (Exception ignored) {

                    }
                } else if (object instanceof Character) {
                    result.add(Integer.valueOf((Character) object));
                } else if (object instanceof Number) {
                    result.add(((Number) object).intValue());
                }
            }

            return result;
        }
    }

    @Override
    public List<Boolean> getBooleanList(String s) {
        List<?> list = this.getList(s);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Boolean> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Boolean) {
                    result.add((Boolean) object);
                } else if (object instanceof String) {
                    if (Boolean.TRUE.toString().equals(object)) {
                        result.add(true);
                    } else if (Boolean.FALSE.toString().equals(object)) {
                        result.add(false);
                    }
                }
            }

            return result;
        }
    }

    @Override
    public List<Double> getDoubleList(String s) {
        List<?> list = this.getList(s);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Double> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Double) {
                    result.add((Double) object);
                } else if (object instanceof String) {
                    try {
                        result.add(Double.valueOf((String) object));
                    } catch (Exception ignored) {

                    }
                } else if (object instanceof Character) {
                    result.add((double) (Character) object);
                } else if (object instanceof Number) {
                    result.add(((Number) object).doubleValue());
                }
            }

            return result;
        }
    }

    @Override
    public List<Float> getFloatList(String s) {
        List<?> list = this.getList(s);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Float> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Float) {
                    result.add((Float) object);
                } else if (object instanceof String) {
                    try {
                        result.add(Float.valueOf((String) object));
                    } catch (Exception ignored) {

                    }
                } else if (object instanceof Character) {
                    result.add((float) (Character) object);
                } else if (object instanceof Number) {
                    result.add(((Number) object).floatValue());
                }
            }

            return result;
        }
    }

    @Override
    public List<Long> getLongList(String s) {
        List<?> list = this.getList(s);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Long> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Long) {
                    result.add((Long) object);
                } else if (object instanceof String) {
                    try {
                        result.add(Long.valueOf((String) object));
                    } catch (Exception ignored) {

                    }
                } else if (object instanceof Character) {
                    result.add((long) (Character) object);
                } else if (object instanceof Number) {
                    result.add(((Number) object).longValue());
                }
            }

            return result;
        }
    }

    @Override
    public List<Byte> getByteList(String path) {
        List<?> list = this.getList(path);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Byte> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Byte) {
                    result.add((Byte) object);
                } else if (object instanceof String) {
                    try {
                        result.add(Byte.valueOf((String)object));
                    } catch (Exception ignored) {

                    }
                }
            }

            return result;
        }
    }

    @Override
    public List<Character> getCharacterList(String path) {
        List<?> list = this.getList(path);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Character> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Character) {
                    result.add((Character) object);
                } else if (object instanceof String) {
                    try {
                        result.add(((String)object).charAt(0));
                    } catch (Exception ignored) {

                    }
                }
            }

            return result;
        }
    }

    @Override
    public List<Short> getShortList(String path) {
        List<?> list = this.getList(path);
        if (list == null) {
            return new ArrayList<>(0);
        } else {
            List<Short> result = new ArrayList<>();

            for (Object object : list) {
                if (object instanceof Short) {
                    result.add((Short) object);
                } else if (object instanceof String) {
                    try {
                        result.add(Short.valueOf((String) object));
                    } catch (Exception ignored) {

                    }
                } else if (object instanceof Number) {
                    result.add(((Number) object).shortValue());
                }
            }

            return result;
        }
    }

    @Override
    public List<Map<?, ?>> getMapList(String path) {
        List<?> list = this.getList(path);
        List<Map<?, ?>> result = new ArrayList<>();
        if (list == null) {
            return result;
        } else {

            for (Object object : list) {
                if (object instanceof Map) {
                    result.add((Map) object);
                }
            }

            return result;
        }
    }

    @Deprecated
    @Override
    public Vector getVector(String s) {
        return null;
    }

    @Deprecated
    @Override
    public Vector getVector(String s, Vector vector) {
        return null;
    }

    @Deprecated
    @Override
    public boolean isVector(String s) {
        return false;
    }

    @Deprecated
    @Override
    public OfflinePlayer getOfflinePlayer(String s) {
        return null;
    }

    @Deprecated
    @Override
    public OfflinePlayer getOfflinePlayer(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Deprecated
    @Override
    public boolean isOfflinePlayer(String s) {
        return false;
    }

    @Deprecated
    @Override
    public ItemStack getItemStack(String s) {
        return getItemStack(s,null);
    }

    @Deprecated
    @Override
    public ItemStack getItemStack(String s, ItemStack def) {
        return null;
    }

    public ExtronStack getExtronStack(String s) {
        return getExtronStack(s,null);
    }

    public ExtronStack getExtronStack(String s, ExtronStack def) {
        Object o = this.get(s);
        return o instanceof ExtronStack ? (ExtronStack) o : o instanceof String ? ItemStackHelper.fromString((String) o,def) : def;
    }

    @Deprecated
    @Override
    public boolean isItemStack(String s) {
        return false;
    }

    @Override
    public Color getColor(String s) {
        return this.getColor(s,null);
    }

    @Override
    public Color getColor(String s, Color def) {
        Object o = this.get(s,def);
        return o == null ? null : o instanceof Color ? (Color) o : o instanceof Integer ? Color.fromRGB((Integer) o) : null;
    }

    @Override
    public boolean isColor(String s) {
        Object o = this.get(s);
        return o instanceof Color || o instanceof Integer;
    }

    @Deprecated
    @Override
    public ConfigurationSection getConfigurationSection(String s) {
        return null;
    }

    public ConfigSection getSection(String path) {
        Object val = this.get(path, null);
        return val instanceof ConfigSection ? (ConfigSection) val : null;
    }

    @Deprecated
    @Override
    public boolean isConfigurationSection(String s) {
        return false;
    }

    @Deprecated
    @Override
    public ConfigurationSection getDefaultSection() {
        return null;
    }

    @Deprecated
    @Override
    public void addDefault(String s, Object o) {

    }

    public Location getLocation(String path, Location def, ExtronWorld w, boolean yawPitch) {
        Object o = this.get(path,def);
        return o == null ? def : o instanceof Location ? (Location)o : parseLocation(o,def,w,yawPitch);
    }

    public Location getLocation(String path, Location def, boolean yawPitch) {
        return this.getLocation(path,def,null,yawPitch);
    }

    public Location getLocation(String path, Location def) {
        return this.getLocation(path,def,false);
    }

    public Location getLocation(String path) {
        return this.getLocation(path,null);
    }

    public static Location parseLocation(Object o, Location def, ExtronWorld w, boolean yawPitch) {
        String s = o.toString();
        String[] arr = s.split(",");
        if (w == null && arr.length < 4) return def;
        if (w != null && arr.length < 3) return def;
        if (yawPitch && w == null && arr.length < 6) return def;
        if (yawPitch && w != null && arr.length < 5) return def;
        try {
            int i = 0;
            ExtronWorld world = w == null ? Main.getWorld(arr[i++]) : w;
            double x = Double.parseDouble(arr[i++]);
            double y = Double.parseDouble(arr[i++]);
            double z = Double.parseDouble(arr[i++]);
            if (yawPitch) {
                float yaw = Float.parseFloat(arr[i++]);
                float pitch = Float.parseFloat(arr[i]);
                return new Location(world.handle,x,y,z,yaw,pitch);
            }
            return new Location(world.handle,x,y,z);
        } catch (Exception e) {
            return def;
        }
    }

    public void setLocation(String path, Location location, boolean world, boolean yawPitch) {
        this.set(path,encodeLocation(location,world,yawPitch));
    }

    public static String encodeLocation(Location loc, boolean world, boolean yawPitch) {
        return String.format("%s%s,%s,%s%s", world ? loc.getWorld().getName() + "," : "", loc.getX(), loc.getY(), loc.getZ(), yawPitch ? String.format(",%s,%s", loc.getYaw(), loc.getPitch()) : "");
    }

    protected ConfigSection create(String key) {
        ConfigSection section = new ConfigSection(key,this);
        this.set(key,section);
        return section;
    }

    public Map<String, Object> toMap() {
        return map;
    }

    public void clear() {
        this.map.clear();
    }

    public String path() {
        if (parent == null) {
            return id;
        } else {
            return parent.path() + "." + id;
        }
    }
}
