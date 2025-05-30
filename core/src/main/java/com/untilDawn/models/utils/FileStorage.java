package com.untilDawn.models.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.untilDawn.models.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class FileStorage {
    private static final String USER_DATA_FILE = "Database/users.json";
    private static final String SETTINGS_DATA_FILE = "DataBase/settings.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String DB_URL = "jdbc:sqlite:Database/users.db";

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        File dbDir = new File("Database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }

        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    user_data TEXT NOT NULL
                )
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean saveUsers(Map<String, User> users) {
        boolean jsonSuccess = saveUsersToJson(users);
        boolean dbSuccess = saveUsersToDatabase(users);

        return jsonSuccess && dbSuccess;
    }

    private static boolean saveUsersToJson(Map<String, User> users) {
        try {
            File file = new File(USER_DATA_FILE);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(users, writer);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to save users to JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean saveUsersToDatabase(Map<String, User> users) {
        String insertSQL = "INSERT OR REPLACE INTO users (username, user_data) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM users");
            }

            for (Map.Entry<String, User> entry : users.entrySet()) {
                pstmt.setString(1, entry.getKey());
                pstmt.setString(2, gson.toJson(entry.getValue()));
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to save users to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, User> loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type userMapType = new TypeToken<Map<String, User>>() {
            }.getType();
            Map<String, User> users = gson.fromJson(reader, userMapType);
            return users != null ? users : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static Map<String, User> loadUsersFromDatabase() {
        Map<String, User> users = new HashMap<>();
        String selectSQL = "SELECT username, user_data FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String userData = rs.getString("user_data");
                User user = gson.fromJson(userData, User.class);
                users.put(username, user);
            }

        } catch (SQLException e) {
            System.err.println("Failed to load users from database: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    public static boolean saveSettings(Map<String, Object> settings) {
        try {
            File file = new File(SETTINGS_DATA_FILE);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(settings, writer);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, Object> loadSettings() {
        File file = new File(SETTINGS_DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type settingsMapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> settings = gson.fromJson(reader, settingsMapType);
            return settings != null ? settings : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
