package com.untilDawn.models.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.untilDawn.models.Game;
import com.untilDawn.models.Player;
import com.untilDawn.models.User;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.enums.Weapons;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GameSaveSystem {
    private static final String SAVE_GAMES_FILE = "DataBase/saved_games.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static boolean saveGame(User user, Game game, Player player, float gameTime) {
        try {
            Map<String, GameSaveData> savedGames = loadAllSavedGames();

            GameSaveData saveData = new GameSaveData();
            saveData.username = user.getUsername();
            saveData.characterName = player.getCharacter().getName();
            saveData.weaponName = game.getSelectedWeapon().getWeapon().getName();
            saveData.playerLevel = player.getLevel();
            saveData.playerXP = player.getXP();
            saveData.playerHealth = player.getPlayerHealth();
            saveData.maxHealth = player.getMaxHealth();
            saveData.playerPosX = player.getPosX();
            saveData.playerPosY = player.getPosY();
            saveData.gameTime = gameTime;
            saveData.timeLimit = game.getTimeLimit();
            saveData.score = game.getScore();
            saveData.hasRegeneration = player.hasRegeneration();
            saveData.damageBonus = player.getDamageBonus();
            saveData.projectileBonus = player.getProjectileBonus();
            saveData.ammoBonus = player.getAmmoBonus();
            saveData.weaponAmmo = game.getSelectedWeapon().getAmmo();
            saveData.saveTimestamp = System.currentTimeMillis();

            for (Abilities ability : Abilities.values()) {
                AbilitySaveData abilityData = new AbilitySaveData(
                    ability.isActive(),
                    ability.getRemainingDuration(),
                    ability.getRemainingCooldown(),
                    hasPlayerAcquiredAbility(player, ability)
                );
                saveData.abilityStates.put(ability.name(), abilityData);
            }

            // Store the save data
            savedGames.put(user.getUsername(), saveData);

            // Write to file
            File file = new File(SAVE_GAMES_FILE);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(savedGames, writer);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GameSaveData loadGame(User user) {
        Map<String, GameSaveData> savedGames = loadAllSavedGames();
        return savedGames.get(user.getUsername());
    }


    public static boolean hasSavedGame(User user) {
        if (user == null || user.isGuest()) {
            return false;
        }

        Map<String, GameSaveData> savedGames = loadAllSavedGames();
        return savedGames.containsKey(user.getUsername());
    }


    public static boolean deleteSavedGame(User user) {
        try {
            Map<String, GameSaveData> savedGames = loadAllSavedGames();
            savedGames.remove(user.getUsername());

            File file = new File(SAVE_GAMES_FILE);
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(savedGames, writer);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static Map<String, GameSaveData> loadAllSavedGames() {
        File file = new File(SAVE_GAMES_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type saveGamesType = new TypeToken<Map<String, GameSaveData>>() {
            }.getType();
            Map<String, GameSaveData> savedGames = gson.fromJson(reader, saveGamesType);
            return savedGames != null ? savedGames : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }


    public static Game restoreGameFromSave(GameSaveData saveData) {
        Characters character = null;
        for (Characters c : Characters.values()) {
            if (c.getName().equals(saveData.characterName)) {
                character = c;
                break;
            }
        }
        if (character == null) {
            character = Characters.Shana;
        }

        // Find weapon
        Weapons weapon = null;
        for (Weapons w : Weapons.values()) {
            if (w.getName().equals(saveData.weaponName)) {
                weapon = w;
                break;
            }
        }
        if (weapon == null) {
            weapon = Weapons.Revolver;
        }

        Player player = new Player(character);
        player.setPlayerHealth(saveData.playerHealth);
        player.setMaxHealth(saveData.maxHealth);
        player.setPosX(saveData.playerPosX);
        player.setPosY(saveData.playerPosY);

        for (int i = 0; i < saveData.playerXP; i++) {
            player.addXP(1);
        }

        if (saveData.hasRegeneration) {
            player.enableRegeneration();
        }

        int vitApps = (saveData.maxHealth - character.getHp());
        for (int i = 0; i < vitApps; i++) {
            player.applyVitality();
        }

        for (int i = 0; i < saveData.projectileBonus; i++) {
            player.applyProcrease();
        }

        for (int i = 0; i < saveData.ammoBonus / 5; i++) {
            player.applyAmocrease();
        }

        if (saveData.abilityStates != null) {
            for (Map.Entry<String, AbilitySaveData> entry : saveData.abilityStates.entrySet()) {
                try {
                    Abilities ability = Abilities.valueOf(entry.getKey());
                    AbilitySaveData abilityData = entry.getValue();

                    if (abilityData.isActive && ability.getType() == Abilities.AbilityType.ACTIVE) {
                        ability.activate();
                    }
                } catch (IllegalArgumentException ignored) {

                }
            }
        }

        Game game = new Game(saveData.timeLimit);
        game.setPlayer(player);
        game.setSelectedWeapon(weapon);
        game.setScore(saveData.score);

        game.getSelectedWeapon().setAmmo(saveData.weaponAmmo);

        return game;
    }

    public static String getSaveInfo(GameSaveData saveData) {
        if (saveData == null) {
            return "No saved game";
        }

        long timeDiff = System.currentTimeMillis() - saveData.saveTimestamp;
        String timeAgo = formatTimeAgo(timeDiff);

        int minutes = (int) (saveData.gameTime / 60);
        int seconds = (int) (saveData.gameTime % 60);

        return String.format("Level %d %s\nTime: %02d:%02d\nSaved %s ago",
            saveData.playerLevel,
            saveData.characterName,
            minutes,
            seconds,
            timeAgo);
    }

    private static String formatTimeAgo(long millisAgo) {
        long seconds = millisAgo / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else {
            return "moments";
        }
    }

    private static boolean hasPlayerAcquiredAbility(Player player, Abilities ability) {
        return switch (ability) {
            case VITALITY -> player.getMaxHealth() > player.getCharacter().getHp();
            case PROCREASE -> player.getProjectileBonus() > 0;
            case AMOCREASE -> player.getAmmoBonus() > 0;
            case REGENERATION -> player.hasRegeneration();
            case DAMAGER, SPEEDY, SHIELD, MULTISHOT -> ability.getCooldownProgress() > 0 || ability.isActive();
        };
    }

    public static class GameSaveData {
        public String username;
        public String characterName;
        public String weaponName;
        public int playerLevel;
        public int playerXP;
        public int playerHealth;
        public int maxHealth;
        public float playerPosX;
        public float playerPosY;
        public float gameTime;
        public int timeLimit;
        public int score;
        public boolean hasRegeneration;
        public int damageBonus;
        public int projectileBonus;
        public int ammoBonus;
        public int weaponAmmo;
        public long saveTimestamp;

        // Ability states
        public Map<String, AbilitySaveData> abilityStates;

        public GameSaveData() {
            abilityStates = new HashMap<>();
        }
    }

    public static class AbilitySaveData {
        public boolean isActive;
        public float remainingDuration;
        public float remainingCooldown;
        public boolean hasBeenUnlocked;

        public AbilitySaveData() {
        }

        public AbilitySaveData(boolean isActive, float remainingDuration, float remainingCooldown, boolean hasBeenUnlocked) {
            this.isActive = isActive;
            this.remainingDuration = remainingDuration;
            this.remainingCooldown = remainingCooldown;
            this.hasBeenUnlocked = hasBeenUnlocked;
        }
    }
}
