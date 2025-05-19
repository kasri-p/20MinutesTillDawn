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
    private static Game currentGame;

    // Settings
    private static String language = "en";
    private static float musicVolume = 0.5f;
    private static String currentMusicTrack = "Pretty Dungeon";
    private static Map<String, String> keybinds = new HashMap<>();
    private static boolean autoReloadEnabled = false;
    private static boolean blackAndWhiteEnabled = false;

    static {
        // Initialize default keybinds
        keybinds.put("Move Up", "W");
        keybinds.put("Move Down", "S");
        keybinds.put("Move Left", "A");
        keybinds.put("Move Right", "D");
        keybinds.put("Shoot", "SPACE");
        keybinds.put("Reload", "R");
        keybinds.put("Pause", "ESC");
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static User getUser(String username) {
        return users.get(username);
    }

    public static void load() {
        users = FileStorage.loadUsers();

        // Load settings
        Map<String, Object> settings = FileStorage.loadSettings();
        if (settings != null) {
            if (settings.containsKey("musicVolume")) {
                musicVolume = ((Number) settings.get("musicVolume")).floatValue();
            }

            if (settings.containsKey("isSFX")) {
                isSFX = (Boolean) settings.get("isSFX");
            }

            if (settings.containsKey("currentMusicTrack")) {
                currentMusicTrack = (String) settings.get("currentMusicTrack");
            }

            if (settings.containsKey("language")) {
                language = (String) settings.get("language");
            }

            if (settings.containsKey("keybinds")) {
                keybinds = (Map<String, String>) settings.get("keybinds");
            }

            if (settings.containsKey("autoReloadEnabled")) {
                autoReloadEnabled = (Boolean) settings.get("autoReloadEnabled");
            }

            if (settings.containsKey("blackAndWhiteEnabled")) {
                blackAndWhiteEnabled = (Boolean) settings.get("blackAndWhiteEnabled");
            }
        }
    }

    public static void save() {
        FileStorage.saveUsers(users);

        // Save settings
        Map<String, Object> settings = new HashMap<>();
        settings.put("musicVolume", musicVolume);
        settings.put("isSFX", isSFX);
        settings.put("currentMusicTrack", currentMusicTrack);
        settings.put("language", language);
        settings.put("keybinds", keybinds);
        settings.put("autoReloadEnabled", autoReloadEnabled);
        settings.put("blackAndWhiteEnabled", blackAndWhiteEnabled);

        FileStorage.saveSettings(settings);
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

    public static void setSFX(boolean enabled) {
        isSFX = enabled;
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

    public static Map<String, User> getUsers() {
        return users;
    }

    public static void setUsers(Map<String, User> users) {
        App.users = users;
    }

    public static String getLanguage() {
        return language;
    }

    public static void changeLanguage() {
        if (language.equals("en")) {
            language = "fr";
        } else {
            language = "en";
        }
    }

    // Settings getters and setters
    public static float getMusicVolume() {
        return musicVolume;
    }

    public static void setMusicVolume(float volume) {
        musicVolume = volume;
    }

    public static String getCurrentMusicTrack() {
        return currentMusicTrack;
    }

    public static void setCurrentMusicTrack(String track) {
        currentMusicTrack = track;
    }

    public static Map<String, String> getKeybinds() {
        return keybinds;
    }

    public static void setKeybinds(Map<String, String> newKeybinds) {
        keybinds = new HashMap<>(newKeybinds);
    }

    public static boolean isAutoReloadEnabled() {
        return autoReloadEnabled;
    }

    public static void setAutoReloadEnabled(boolean enabled) {
        autoReloadEnabled = enabled;
    }

    public static boolean isBlackAndWhiteEnabled() {
        return blackAndWhiteEnabled;
    }

    public static void setBlackAndWhiteEnabled(boolean enabled) {
        blackAndWhiteEnabled = enabled;
    }

    public static Game getGame() {
        return currentGame;
    }

    public static void setGame(Game game) {
        currentGame = game;
    }

    public void toggleSFX() {
        isSFX = !isSFX;
    }
}
