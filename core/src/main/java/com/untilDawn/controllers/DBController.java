package com.untilDawn.controllers;

import com.untilDawn.models.App;
import com.untilDawn.models.User;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBController {
    private static final String DB_URL = "jdbc:h2:./database";
    private static final String USER = "ks";
    private static final String PASSWORD = "";

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to the database");

            createTable(connection);

            loadUsers();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection connection) {
//        String dropSql = "DROP TABLE IF EXISTS USERS";
        String createSql = "ALTER TABLE users " +
            "ADD COLUMN IF NOT EXISTS username VARCHAR(50), " +
            "ADD COLUMN IF NOT EXISTS password VARCHAR(50), " +
            "ADD COLUMN IF NOT EXISTS SecurityQuestionIndex INT, " +
            "ADD COLUMN IF NOT EXISTS SecurityAnswer VARCHAR(100), " +
            "ADD COLUMN IF NOT EXISTS isGuest BOOLEAN, " +
            "ADD COLUMN IF NOT EXISTS score INT, " +
            "ADD COLUMN IF NOT EXISTS avatarPath VARCHAR(255), " +
            "ADD COLUMN IF NOT EXISTS deaths INT, " +
            "ADD COLUMN IF NOT EXISTS kills INT";

        String insertSql = "INSERT INTO users " +
            "(username, password, SecurityQuestionIndex, SecurityAnswer, isGuest, score, avatarPath, deaths, kills) " +
            "VALUES ('player1', 'securepass123', 2, 'childhood pet', false, 1000, '/avatars/default.png', 0, 5)";

        try (Statement stmt = connection.createStatement()) {
//            stmt.executeUpdate(dropSql);
            stmt.executeUpdate(createSql);
//            stmt.executeUpdate(insertSql);
            System.out.println("Table 'users' dropped (if existed) and recreated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveUsers() {
        Map<String, User> users = App.getUsers();
        if (users.isEmpty()) {
            System.out.println("No users to save.");
            return;
        }

        String sql = "MERGE INTO users KEY(username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (User user : users.values()) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setInt(3, user.getSecurityQuestionIndex());
                pstmt.setString(4, user.getSecurityAnswer());
                pstmt.setBoolean(5, user.isGuest());
                pstmt.setInt(6, user.getScore());
                pstmt.setString(7, user.getAvatarPath());
                pstmt.setInt(8, user.getDeaths());
                pstmt.setInt(9, user.getKills());

                pstmt.executeUpdate();
            }

            System.out.println("Users saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadUsers() {
        Map<String, User> users = new HashMap<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("here");
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setSecurityQuestionIndex(rs.getInt("SecurityQuestionIndex"));
                user.setSecurityAnswer(rs.getString("SecurityAnswer"));
                users.put(user.getUsername(), user);
                System.out.println(user);
            }
            System.out.println("Users loaded successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Disconnected from the database");

            saveUsers();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
