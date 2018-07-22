package com.extron.network.api.data;

import com.extron.network.api.Main;

import java.util.ArrayList;
import java.util.List;

public abstract class DataTable<K> {

    private final String name;
    protected List<DataColumn> cols;
    protected String primaryKey;

    public DataTable(String name) {
        this.name = name;
        this.cols = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addColumn(DataColumn column) {
        this.cols.add(column);
    }

    public List<DataColumn> getColumns() {
        return cols;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Object get(String col, K key) {
        for (DataColumn c : cols) {
            if (c.name.equalsIgnoreCase(col)) {
                return c.data.getOrDefault(key.toString(),c.def);
            }
        }
        return null;
    }

    public boolean getBoolean(String col, K key) {
        for (DataColumn c : cols) {
            if (c.name.equalsIgnoreCase(col)) {
                return Boolean.parseBoolean(c.data.getOrDefault(key.toString(),c.def).toString());
            }
        }
        return false;
    }

    public String getString(String col, K key) {
        for (DataColumn c : cols) {
            if (c.name.equalsIgnoreCase(col)) {
                return c.data.getOrDefault(key.toString(),c.def).toString();
            }
        }
        return "";
    }

    public int getInt(String col, K key) {
        for (DataColumn c : cols) {
            if (c.name.equalsIgnoreCase(col)) {
                return Integer.parseInt(c.data.getOrDefault(key.toString(),c.def).toString());
            }
        }
        return 0;
    }

    public void set(String col, K key, Object value, boolean permanent) {
        for (DataColumn c : cols) {
            if (c.name.equalsIgnoreCase(col)) {
                c.data.put(key.toString(),value);
                if (permanent) Main.getDatabaseManager().setValue(this,c,key.toString());
            }
        }
    }

    public void set(String col, K key, Object value) {
        this.set(col, key, value,true);
    }

    public DataColumn getColumn(String name) {
        for (DataColumn c : cols) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
