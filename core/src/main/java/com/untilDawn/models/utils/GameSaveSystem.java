package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.untilDawn.models.*;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.enums.EnemyType;
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
            saveData.kills = player.getKills();

            // Save enemies with better serialization
            saveData.enemies.clear();
            if (game.getEnemies() != null) {
                int enemyId = 0;
                for (Enemy enemy : game.getEnemies()) {
                    if (enemy != null && enemy.isActive()) {
                        EnemySaveData enemyData = createEnemySaveData(enemy);
                        if (enemyData != null) {
                            saveData.enemies.put(enemyId++, enemyData);
                        }
                    }
                }
            }

            // Save ability states
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

                Gdx.app.log("GameSaveSystem", "Game saved successfully - Enemies: " +
                    saveData.enemies.size() + ", Time: " + gameTime);
                return true;
            }

        } catch (Exception e) {
            Gdx.app.error("GameSaveSystem", "Failed to save game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static EnemySaveData createEnemySaveData(Enemy enemy) {
        try {
            EnemySaveData enemyData = new EnemySaveData(
                enemy.getType().name(),
                enemy.getPosX(),
                enemy.getPosY(),
                enemy.getHealth(),
                enemy.isActive()
            );

            // Save additional data for special enemy types
            if (enemy instanceof ElderBoss) {
                enemyData.isElderBoss = true;
                ElderBoss elder = (ElderBoss) enemy;
                enemyData.elderBossState = String.valueOf(elder.getCurrentState());
                enemyData.isBarrierActive = elder.isBarrierActive();

                Gdx.app.log("GameSaveSystem", "Saving Elder Boss with state: " + enemyData.elderBossState);
            }

            return enemyData;

        } catch (Exception e) {
            Gdx.app.error("GameSaveSystem", "Failed to create enemy save data: " + e.getMessage());
            return null;
        }
    }

    public static GameSaveData loadGame(User user) {
        Map<String, GameSaveData> savedGames = loadAllSavedGames();
        GameSaveData saveData = savedGames.get(user.getUsername());

        if (saveData != null) {
            Gdx.app.log("GameSaveSystem", "Loaded save data for " + user.getUsername() +
                " - Enemies: " + saveData.enemies.size() + ", Time: " + saveData.gameTime);
        }

        return saveData;
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
        try {
            // Find character
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

            // Create player with restored state
            Player player = new Player(character);
            player.setPlayerHealth(saveData.playerHealth);
            player.setMaxHealth(saveData.maxHealth);
            player.setPosX(saveData.playerPosX);
            player.setPosY(saveData.playerPosY);

            // Restore XP and level
            if (saveData.playerXP > 0) {
                player.addXP(saveData.playerXP);
            }

            // Restore kills
            if (saveData.kills > 0) {
                for (int i = 0; i < saveData.kills; i++) {
                    player.addKill();
                }
            }

            // Restore player abilities
            restorePlayerAbilities(player, saveData, character);

            // Create game with restored state
            Game game = new Game(saveData.timeLimit);
            game.setPlayer(player);
            game.setSelectedWeapon(weapon);
            game.setScore(saveData.score);
            game.setGameTime(saveData.gameTime);

            // Restore weapon ammo
            game.getSelectedWeapon().setAmmo(saveData.weaponAmmo);

            // Restore enemies
            restoreEnemies(game, saveData);

            // Restore ability states
            restoreAbilityStates(saveData);

            Gdx.app.log("GameSaveSystem", "Game restored successfully - Enemies: " +
                game.getEnemies().size() + ", Time: " + saveData.gameTime);

            return game;

        } catch (Exception e) {
            Gdx.app.error("GameSaveSystem", "Failed to restore game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void restorePlayerAbilities(Player player, GameSaveData saveData, Characters character) {
        // Restore regeneration
        if (saveData.hasRegeneration) {
            player.enableRegeneration();
        }

        // Restore vitality (max health increases)
        int vitApps = (saveData.maxHealth - character.getHp());
        for (int i = 0; i < vitApps; i++) {
            player.applyVitality();
        }

        // Restore projectile bonuses
        for (int i = 0; i < saveData.projectileBonus; i++) {
            player.applyProcrease();
        }

        // Restore ammo bonuses
        for (int i = 0; i < saveData.ammoBonus / 5; i++) {
            player.applyAmocrease();
        }
    }

    private static void restoreEnemies(Game game, GameSaveData saveData) {
        game.getEnemies().clear();

        if (saveData.enemies != null && !saveData.enemies.isEmpty()) {
            for (EnemySaveData enemyData : saveData.enemies.values()) {
                try {
                    Enemy enemy = createEnemyFromSaveData(enemyData);
                    if (enemy != null && enemy.isActive()) {
                        game.addEnemy(enemy);
                    }
                } catch (Exception e) {
                    Gdx.app.error("GameSaveSystem", "Failed to restore enemy: " + e.getMessage());
                }
            }

            Gdx.app.log("GameSaveSystem", "Restored " + game.getEnemies().size() + " enemies");
        }
    }

    private static Enemy createEnemyFromSaveData(EnemySaveData enemyData) {
        try {
            EnemyType enemyType = EnemyType.valueOf(enemyData.type);
            Enemy enemy;

            // Create appropriate enemy type
            if (enemyData.isElderBoss && enemyType == EnemyType.ELDER) {
                // Use default map size for Elder Boss (this will be overridden by EnemyController)
                float mapWidth = 4096f;
                float mapHeight = 4096f;
                enemy = new ElderBoss(enemyData.posX, enemyData.posY, mapWidth, mapHeight);

                Gdx.app.log("GameSaveSystem", "Restored Elder Boss at (" +
                    enemyData.posX + ", " + enemyData.posY + ")");
            } else {
                enemy = new Enemy(enemyType, enemyData.posX, enemyData.posY);
            }

            // Restore health by dealing damage
            int healthDiff = enemyType.getHealth() - enemyData.health;
            for (int i = 0; i < healthDiff && enemy.isActive(); i++) {
                enemy.hit(1);
            }

            return enemy;

        } catch (IllegalArgumentException e) {
            Gdx.app.error("GameSaveSystem", "Unknown enemy type: " + enemyData.type);
            return null;
        } catch (Exception e) {
            Gdx.app.error("GameSaveSystem", "Failed to create enemy from save data: " + e.getMessage());
            return null;
        }
    }

    private static void restoreAbilityStates(GameSaveData saveData) {
        if (saveData.abilityStates != null) {
            for (Map.Entry<String, AbilitySaveData> entry : saveData.abilityStates.entrySet()) {
                try {
                    Abilities ability = Abilities.valueOf(entry.getKey());
                    AbilitySaveData abilityData = entry.getValue();

                    // Reset the ability first
                    ability.reset();

                    // Restore active state if it was active
                    if (abilityData.isActive && ability.getType() == Abilities.AbilityType.ACTIVE) {
                        ability.activate();
                        // Note: The exact remaining duration/cooldown will be handled by the ability system
                    }
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("GameSaveSystem", "Unknown ability: " + entry.getKey());
                }
            }
        }
    }

    public static String getSaveInfo(GameSaveData saveData) {
        if (saveData == null) {
            return "No saved game";
        }

        long timeDiff = System.currentTimeMillis() - saveData.saveTimestamp;
        String timeAgo = formatTimeAgo(timeDiff);

        int minutes = (int) (saveData.gameTime / 60);
        int seconds = (int) (saveData.gameTime % 60);

        return String.format("Level %d %s\nTime: %02d:%02d\nEnemies: %d\nSaved %s ago",
            saveData.playerLevel,
            saveData.characterName,
            minutes,
            seconds,
            saveData.enemies.size(),
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
        public int kills;

        // Ability states
        public Map<String, AbilitySaveData> abilityStates;

        // Enemies
        public Map<Integer, EnemySaveData> enemies;

        public GameSaveData() {
            abilityStates = new HashMap<>();
            enemies = new HashMap<>();
        }
    }

    public static class EnemySaveData {
        public String type;
        public float posX;
        public float posY;
        public int health;
        public boolean isActive;
        public boolean isElderBoss;
        public String elderBossState;
        public boolean isBarrierActive;

        public EnemySaveData() {
        }

        public EnemySaveData(String type, float posX, float posY, int health, boolean isActive) {
            this.type = type;
            this.posX = posX;
            this.posY = posY;
            this.health = health;
            this.isActive = isActive;
            this.isElderBoss = false;
            this.elderBossState = null;
            this.isBarrierActive = false;
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
