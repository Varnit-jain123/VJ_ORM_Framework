package com.vj.orm.core;
import com.vj.orm.config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.vj.orm.annotation.*;
public class ConnectionManager {
    private static Connection connection = null;

    public static Connection getConnection(DBConfig config) throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            Class.forName(config.getJdbcDriver());
            connection = DriverManager.getConnection(config.getConnectionUrl(), config.getUsername(), config.getPassword());
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
