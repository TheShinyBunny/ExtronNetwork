package com.extron.network.api.data;

import com.extron.network.api.utils.JsonContainer;
import com.extron.network.api.utils.Savable;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

public abstract class JsonData<T> extends JsonContainer implements Savable<String> {

    protected DataTable<T> table;
    protected String colName;

    public JsonData(DataTable<T> table, String colName) {
        super();
        this.table = table;
        this.colName = colName;
    }

    public abstract T getOwner();

    @Override
    public void load(String json) {
        System.out.println("loading json:");
        System.out.println(json);
        this.json = JsonContainer.parse(json);
    }

}
