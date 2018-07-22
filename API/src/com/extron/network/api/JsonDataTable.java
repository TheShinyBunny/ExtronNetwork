package com.extron.network.api;

import com.extron.network.api.data.ColumnType;
import com.extron.network.api.data.DataColumn;
import com.extron.network.api.data.DataTable;
import com.extron.network.api.players.ExtronPlayer;

public class JsonDataTable<T> extends DataTable<T> {
    public JsonDataTable(String name) {
        super(name);
        this.cols.add(new DataColumn("uuid",ColumnType.STRING(200),false,"<uuid>"));
        this.cols.add(new DataColumn("data",ColumnType.JSON(),false,"{}"));
        this.primaryKey = "uuid";
    }
}
