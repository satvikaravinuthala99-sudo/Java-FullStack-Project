
package com.echovault.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/echovault_db?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";

    // Enter your actual MySQL root password here
    private static final String PASSWORD = "Rvs@9441";

    public static Connection getConnection() throws SQLException {

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC Driver not found. Check Maven Dependencies and Deployment Assembly.",
                    e
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
