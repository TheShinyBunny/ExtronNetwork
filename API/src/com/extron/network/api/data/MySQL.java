package com.extron.network.api.data;

import com.extron.network.api.Main;

import java.sql.*;

public class MySQL {
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;
    protected Connection connection;

    /**
     * Creates a new MySQL instance
     *
     * @param hostname
     *            Name of the host
     * @param port
     *            Port number
     * @param username
     *            Username
     * @param password
     *            Password
     */
    public MySQL(String hostname, String port, String username,
                 String password) {
        this(hostname, port, null, username, password);
    }

    /**
     * Creates a new MySQL instance for a specific database
     *
     * @param hostname
     *            Name of the host
     * @param port
     *            Port number
     * @param database
     *            Database name
     * @param username
     *            Username
     * @param password
     *            Password
     */
    public MySQL(String hostname, String port, String database,
                 String username, String password) {
        this.connection = null;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    public Connection openConnection() throws SQLException,
            ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }

        String connectionURL = "jdbc:mysql://"
                + this.hostname + ":" + this.port;
        if (database != null) {
            connectionURL = connectionURL + "/" + this.database;
        }

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(connectionURL,
                this.user, Main.getMainConfig().getString("database.password"));
        return connection;
    }

    /**
     * Checks if a connection is open with the database
     *
     * @return true if the connection is open
     * @throws SQLException
     *             if the connection cannot be checked
     */
    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    /**
     * Gets the connection with the database
     *
     * @return Connection with the database, null if none
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the connection with the database
     *
     * @return true if successful
     * @throws SQLException
     *             if the connection cannot be closed
     */
    public boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }


    /**
     * Executes a SQL Query<br>
     *
     * If the connection is closed, it will be opened
     *
     * @param query
     *            Query to be run
     * @return the results of the query
     * @throws SQLException
     *             If the query cannot be executed
     * @throws ClassNotFoundException
     *             If the driver cannot be found; see {@link #openConnection()}
     */
    public ResultSet querySQL(String query) throws SQLException,
            ClassNotFoundException {
        if (!checkConnection()) {
            openConnection();
        }

        Statement statement = connection.createStatement();

        ResultSet result = statement.executeQuery(query);

        return result;
    }

    /**
     * Executes an Update SQL Query<br>
     * See {@link Statement#executeUpdate(String)}<br>
     * If the connection is closed, it will be opened
     *
     * @param query
     *            Query to be run
     * @return Result Code, see {@link Statement#executeUpdate(String)}
     * @throws SQLException
     *             If the query cannot be executed
     * @throws ClassNotFoundException
     *             If the driver cannot be found; see {@link #openConnection()}
     */
    public int updateSQL(String query) throws SQLException,
            ClassNotFoundException {
        if (!checkConnection()) {
            openConnection();
        }

        Statement statement = connection.createStatement();

        int result = statement.executeUpdate(query);

        return result;
    }

}
