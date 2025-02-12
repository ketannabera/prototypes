package com.k10.connectionPool;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/employee";

    public static Connection newConnection() {
        try {

            System.out.println("**** Attempting connection - Thread: " + Thread.currentThread().getId());

            // Register the JDBC driver (optional in newer versions)
            Class.forName("org.postgresql.Driver");

            // Create the connection
            Connection connection = DriverManager.getConnection(URL);

            System.out.println("**** GOT connection - Thread: " + Thread.currentThread().getId());

            return connection;

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    // Simple usage example
    public static String executeQuery(Connection connection) {
        try (Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM employee LIMIT 1");
            resultSet.next();
            return resultSet.getString("name");

        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
    }
}