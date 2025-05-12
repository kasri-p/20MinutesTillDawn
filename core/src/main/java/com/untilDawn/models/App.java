package com.untilDawn.models;

import com.untilDawn.models.utils.FileStorage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static Map<String, User> users = new HashMap<>();
    private static User loggedInUser;
    private static boolean isSFX = true;
    private static Map<User, Game> games = new HashMap<>();

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

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        String newHash = hashPassword(plainPassword);
        return newHash != null && newHash.equals(storedHash);
    }

    public static boolean isSFX() {
        return isSFX;
    }

    public static void removeUser(User user) {
        users.remove(user.getUsername());
    }

    public static Game getGame(User user) {
        return games.get(user);
    }

    public static void addGame(Game game, User user) {
        if (games.get(user) == null) {
            games.put(user, game);
        } else {
            games.remove(user);
            games.put(user, game);
        }
    }

    public void toggleSFX() {
        isSFX = !isSFX;
    }
}
