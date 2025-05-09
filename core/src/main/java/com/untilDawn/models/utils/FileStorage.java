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
    private static final String USER_DATA_FILE = "core/src/main/DB/users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean saveUsers(Map<String, User> users) {
        try {
            File file = new File(USER_DATA_FILE);
            file.getParentFile().mkdirs();

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
            file.getParentFile().mkdirs();
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
}
