package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    public static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS01;databaseName=FlowDatabase;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";


    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

}

