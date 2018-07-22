package com.extron.network.api.data;

public class ColumnType {

    private final String sql;

    public ColumnType(String sqlStatement) {
        this.sql = sqlStatement;
    }

    public static ColumnType BOOLEAN() {
        return new ColumnType("ENUM('true','false')");
    }

    public static ColumnType JSON() {
        return new ColumnType("LONGTEXT");
    }

    public String toSql() {
        return sql;
    }

    public static ColumnType STRING(int maxChars) {
        return new ColumnType("VARCHAR(" + maxChars + ")");
    }

    public static ColumnType INT() {
        return new ColumnType("INT");
    }

    public static ColumnType ENUM(Class<? extends Enum> e) {
        StringBuilder b = new StringBuilder();
        b.append("ENUM(");
        for (Enum en : e.getEnumConstants()) {
            b.append("'");
            b.append(en.toString());
            b.append("',");
        }
        b.delete(b.length() - 1,b.length());
        return new ColumnType(b.append(")").toString());
    }

}
