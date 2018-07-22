package com.extron.network.api.data;

import com.extron.network.api.Main;
import com.extron.network.api.config.Config;
import com.extron.network.api.players.ExtronPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private MySQL db;

    private List<DataTable<?>> dataTables = new ArrayList<>();

    public void addTable(DataTable<?> table) {
        this.dataTables.add(table);
    }

    public void setupDB() throws Exception {
        this.db = this.createDatabase();
        db.openConnection();
        for (DataTable<?> table : dataTables) {
            Statement statement = db.getConnection().createStatement();
            StringBuilder b = new StringBuilder();
            for (DataColumn c : table.cols) {
                b.append(c.name);
                b.append(" " + c.type.toSql());
                b.append(c.nulls ? "" : " NOT NULL");
                b.append(",");
            }
            b.append("PRIMARY KEY (" + table.getPrimaryKey() + ")");
            statement.executeUpdate("create TABLE IF NOT EXISTS " + table.getName() + " (" + b.toString() + ");");
            statement.close();
            Statement check = db.getConnection().createStatement();
            ResultSet res = check.executeQuery("SELECT * FROM " + table.getName());
            for (Object o : table.cols) {
                DataColumn c = (DataColumn) o;
                try {
                    int i = res.findColumn(c.name);
                } catch (SQLException unused) {
                    Statement addCol = db.getConnection().createStatement();
                    addCol.executeUpdate("ALTER TABLE " + table.getName() + " ADD " + c.name + " " + c.type.toSql() + (c.nulls ? "" : " NOT NULL") + ";");
                    addCol.close();
                }
            }
            check.close();
        }
        System.out.println("MySql connected!");
    }

    private MySQL createDatabase() throws Exception {
        Config config = Main.getMainConfig();
        if (config.getString("database.hostname") == null) {
            throw new Exception("config.yml doesn't contain database.hostname!");
        }
        if (config.getInt("database.port") == 0) {
            throw new Exception("config.yml doesn't contain database.port!");
        }
        if (config.getString("database.name") == null) {
            throw new Exception("config.yml doesn't contain database.name!");
        }
        if (config.getString("database.username") == null) {
            throw new Exception("config.yml doesn't contain database.username!");
        }
        return new MySQL(
                config.getString("database.hostname"),
                String.valueOf(config.getInt("database.port")),
                config.getString("database.name"),
                config.getString("database.username"),
                config.getString("database.password")
            );
    }

    public void closeDB() {
        checkState();
        try {
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlayerData(ExtronPlayer p) {
        checkState();
        ResultSet result;
        try {
            Statement statement = db.getConnection().createStatement();
            result = statement.executeQuery("SELECT * FROM players WHERE uuid='" + p.getUUID().toString() + "';");
            if (result.next()) {
                p.getData().load(result.getString("data"));
            } else {
                this.createPlayerData(p);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerData(ExtronPlayer p) {
        checkState();
        try {
            if (this.tableContains(Main.getPlayersData(),"uuid",p.getUUID().toString())) return;
            Statement statement = db.getConnection().createStatement();
            statement.executeUpdate("INSERT INTO players (uuid,data) VALUES ('" + p.getUUID() + "','{\"name\":\"" + p.getName() + "\"}');");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean tableContains(DataTable<?> table, String key, Object value) throws SQLException {
        checkState();
        Statement check = db.getConnection().createStatement();
        ResultSet result = check.executeQuery("SELECT * FROM " + table.getName() + " WHERE " + key + "=" + (value instanceof Number ? value : "\'" + value.toString() + "\'") + ";");
        return result.next();
    }

    private void checkState() {
        try {
            if (db == null) {
                throw new Exception("database is not registered yet!");
            }
            if (!db.checkConnection()) db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllPlayerUUIDs() {
        checkState();
        List<String> uuids = new ArrayList<>();
        try {
            Statement statement = db.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM players");
            while (result.next()) {
                uuids.add(result.getString("uuid"));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuids;
    }

    public void setValue(DataTable<?> table, DataColumn column, String key) {
        checkState();
        try {
            Statement statement = db.getConnection().createStatement();
            Object o = column.data.getOrDefault(key,column.def);
            String update = "UPDATE " + table.getName() + " SET " + column.name + "=" + (o instanceof Number ? o : "\'" + o.toString() + "\'") + " WHERE " + table.getPrimaryKey() + "=" + "\'" + key + "\'" + ";";
            System.out.println("setting getValue: " + update);
            statement.executeUpdate(update);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataTable<?> getTable(String name) {
        for (DataTable<?> table : dataTables) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }
}
