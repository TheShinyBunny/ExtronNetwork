package com.extron.network.api.data;

import java.util.HashMap;
import java.util.Map;

public class DataColumn {

    public final boolean nulls;
    public final ColumnType type;
    public final String name;
    public final Object def;

    public Map<String,Object> data = new HashMap<>();

    public DataColumn(String name, ColumnType type, boolean allowNull, Object def) {
        this.name = name;
        this.type = type;
        this.nulls = allowNull;
        this.def = def;
    }
}
