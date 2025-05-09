package com.untilDawn.models;

import com.untilDawn.models.utils.FileStorage;

import java.util.Map;

public class App {
    private static Map<String, User> users;
    private static User loggedInUser;

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static User getUser(String username) {
        return users.get(username);
    }

    public static void load() {
        users = FileStorage.loadUsers();
    }

    public static void save() {
        FileStorage.saveUsers(users);
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void logout() {
        loggedInUser = null;
    }
}
