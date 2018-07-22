package com.extron.network.api.utils;

import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonContainer implements DataObject {
    protected JsonObject json;

    public JsonContainer(JsonObject obj) {
        this.json = obj;
    }

    public JsonContainer() {
        this.json = new JsonObject();
    }

    public JsonObject getJson() {
        return json;
    }

    public boolean getBoolean(String key, boolean def) {
        if (json.has(key)) {
            JsonElement e = json.get(key);
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean()) {
                return e.getAsJsonPrimitive().getAsBoolean();
            }
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).getBoolean(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    public int getInt(String key, int def) {
        if (json.has(key)) {
            JsonElement e = json.get(key);
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) {
                return e.getAsJsonPrimitive().getAsNumber().intValue();
            }
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).getInt(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    public String getString(String key, String def) {
        if (json.has(key)) {
            JsonElement e = json.get(key);
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
                return e.getAsJsonPrimitive().getAsString();
            }
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).getString(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    @Override
    public String getString(String key) {
        return this.getString(key,null);
    }

    public List<String> getStringList(String key, List<String> def) {
        if (json.has(key)) {
            JsonElement e = json.get(key);
            if (e.isJsonArray() && areAllStrings(e.getAsJsonArray())) {
                return this.toStringList(e.getAsJsonArray());
            }
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).getStringList(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    public List<String> getStringList(String key) {
        return this.getStringList(key,null);
    }

    private List<String> toStringList(JsonArray array) {
        List<String> list = new ArrayList<>();
        for (JsonElement e : array) {
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
                list.add(e.getAsJsonPrimitive().getAsString());
            }
        }
        return list;
    }

    private static boolean areAllStrings(JsonArray array) {
        boolean flag = true;
        for (JsonElement e : array) {
            if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public void set(String key, Object value) {
        System.out.println("setting json value '" + key + "' = " + value);
        if (key.contains(".")) {
            String key2 = key.substring(key.indexOf(".")+1,key.length());
            if (this.json.has(key.substring(0,key.indexOf(".")))) {
                JsonElement e = json.get(key.substring(0,key.indexOf(".")));
                if (e.isJsonObject()) {
                    set(e.getAsJsonObject(),key2,value);
                }
            } else {
                if (value != null) {
                    JsonObject o = new JsonObject();
                    this.json.add(key.substring(0, key.indexOf(".")), o);
                    set(o, key2, value);
                }
            }
        } else {
            if (value == null) {
                if (json.has(key)) {
                    this.json.remove(key);
                }
                return;
            }
            if (value instanceof String) {
                this.json.addProperty(key,(String)value);
            } else if (value instanceof Number) {
                this.json.addProperty(key,(Number)value);
            } else if (value instanceof Boolean) {
                this.json.addProperty(key,(Boolean)value);
            } else if (value instanceof Collection) {
                this.json.add(key,toJsonArray((Collection)value));
            } else if (value instanceof JsonObject) {
                this.json.add(key,(JsonObject)value);
            } else if (value instanceof JsonContainer) {
                this.json.add(key,((JsonContainer)value).json);
            } else {
                this.json.addProperty(key,value.toString());
            }
        }
    }

    public static void set(JsonObject obj, String key, Object value) {
        System.out.println("setting json value '" + key + "' = " + value);
        if (key.contains(".")) {
            String key2 = key.substring(key.indexOf(".")+1,key.length());
            if (obj.has(key.substring(0,key.indexOf(".")))) {
                JsonElement e = obj.get(key.substring(0,key.indexOf(".")));
                if (e.isJsonObject()) {
                    set(e.getAsJsonObject(),key2,value);
                }
            } else {
                if (value != null) {
                    JsonObject o = new JsonObject();
                    obj.add(key.substring(0, key.indexOf(".")), o);
                    set(o, key2, value);
                }
            }
        } else {
            if (value == null) {
                if (obj.has(key)) {
                    obj.remove(key);
                }
                return;
            }
            if (value instanceof String) {
                obj.addProperty(key,(String)value);
            } else if (value instanceof Number) {
                obj.addProperty(key,(Number)value);
            } else if (value instanceof Boolean) {
                obj.addProperty(key,(Boolean)value);
            } else if (value instanceof List) {
                obj.add(key,toJsonArray((List)value));
            } else if (value instanceof JsonObject) {
                obj.add(key,(JsonObject)value);
            } else if (value instanceof JsonContainer) {
                obj.add(key,((JsonContainer)value).json);
            } else {
                obj.addProperty(key,value.toString());
            }
        }
    }

    public static JsonArray toJsonArray(Collection list) {
        JsonArray a = new JsonArray();
        for (Object obj : list) {
            if (obj instanceof String) {
                a.add(new JsonPrimitive((String) obj));
            } else if (obj instanceof Number) {
                a.add(new JsonPrimitive((Number) obj));
            } else if (obj instanceof Boolean) {
                a.add(new JsonPrimitive((Boolean) obj));
            } else if (obj instanceof List) {
                a.addAll(toJsonArray((List<?>)obj));
            } else if (obj instanceof JsonObject) {
                a.add((JsonObject)obj);
            } else if (obj instanceof JsonContainer) {
                a.add(((JsonContainer)obj).json);
            }
        }
        return a;
    }

    public JsonContainer getJsonObject(String key) {
        return this.getJsonObject(key,null);
    }

    public JsonContainer getJsonObject(String key, JsonContainer def) {
        if (json.has(key)) {
            JsonElement e = json.get(key);
            if (e.isJsonObject()) {
                return new JsonContainer(e.getAsJsonObject());
            }
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).getJsonObject(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    public List<JsonContainer> getObjectList(String key,List<JsonContainer> def) {
        if (json.has(key)) {
            JsonElement e = json.get(key);
            if (e.isJsonArray() && this.areAllObjects(e.getAsJsonArray())) {
                return this.toObjectList(e.getAsJsonArray());
            }
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).getObjectList(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    @Override
    public Object get(String key, Object def) {
        if (json.has(key)) {
            return json.get(key);
        } else {
            if (key.contains(".")) {
                if (json.has(key.substring(0,key.indexOf('.')))) {
                    JsonElement e = json.get(key.substring(0,key.indexOf('.')));
                    if (e.isJsonObject()) {
                        return new JsonContainer(e.getAsJsonObject()).get(key.substring(key.indexOf('.')+1,key.length()),def);
                    }
                }
            }
        }
        return def;
    }

    private List<JsonContainer> toObjectList(JsonArray array) {
        List<JsonContainer> list = new ArrayList<>();
        for (JsonElement e : array) {
            if (e.isJsonObject()) {
                list.add(new JsonContainer(e.getAsJsonObject()));
            }
        }
        return list;
    }

    private boolean areAllObjects(JsonArray array) {
        boolean flag = true;
        for (JsonElement e : array) {
            if (!e.isJsonObject()) {
                flag = false;
            }
        }
        return flag;
    }

    public List<JsonContainer> getObjectList(String key) {
        return this.getObjectList(key,null);
    }

    @Override
    public String toString() {
        return json.toString();
    }

    public static JsonObject parse(String json) {
        JsonParser parser = new JsonParser();
        JsonElement e = parser.parse(json);
        return e.getAsJsonObject();
    }
}
