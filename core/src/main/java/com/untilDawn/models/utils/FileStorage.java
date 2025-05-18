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
import java.util.HashMap;
import java.util.Map;

public class FileStorage {
    private static final String USER_DATA_FILE = "Database/users.json";
    private static final String SETTINGS_DATA_FILE = "DataBase/settings.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean saveUsers(Map<String, User> users) {
        try {
            File file = new File(USER_DATA_FILE);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(users, writer);
                return true;
            }
        } catch (IOException e) {
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
