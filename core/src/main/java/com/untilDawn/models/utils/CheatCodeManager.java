package com.untilDawn.models.utils;

import com.badlogic.gdx.Gdx;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.Player;

import java.util.HashMap;
import java.util.Map;

public class CheatCodeManager {
    // Cheat code strings
    public static final String REDUCE_TIME = "TIMEREDUCTION";
    public static final String LEVEL_UP = "LEVELUP";
    public static final String HEAL_PLAYER = "HEALME";
    public static final String BOSS_FIGHT = "BOSSMODE";
    public static final String GOD_MODE = "GODMODE";
    private static CheatCodeManager instance;
    private Map<String, CheatCode> cheatCodes;
    private GameController gameController;
    private Player player;

    private CheatCodeManager() {
        initializeCheatCodes();
    }

    public static CheatCodeManager getInstance() {
        if (instance == null) {
            instance = new CheatCodeManager();
        }
        return instance;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        this.player = gameController.getPlayerController().getPlayer();
    }

    private void initializeCheatCodes() {
        cheatCodes = new HashMap<>();

        // Reduce time by 1 minute
        cheatCodes.put(REDUCE_TIME, new CheatCode(
            "TIMEREDUCTION",
            "Time Reduction",
            "Reduces remaining game time by 1 minute",
            () -> reduceTime()
        ));

        // Level up player
        cheatCodes.put(LEVEL_UP, new CheatCode(
            "LEVELUP",
            "Level Up",
            "Instantly levels up the player with full animations",
            () -> levelUpPlayer()
        ));

        // Heal player (only if health is empty)
        cheatCodes.put(HEAL_PLAYER, new CheatCode(
            "HEALME",
            "Heal Player",
            "Restores player health to full (only if health is empty)",
            () -> healPlayer()
        ));

        // Boss fight mode
        cheatCodes.put(BOSS_FIGHT, new CheatCode(
            "BOSSMODE",
            "Boss Fight",
            "Triggers a boss fight encounter",
            () -> triggerBossFight()
        ));

        // God mode (bonus cheat)
        cheatCodes.put(GOD_MODE, new CheatCode(
            "GODMODE",
            "God Mode",
            "Makes player invincible for 30 seconds",
            () -> activateGodMode()
        ));
    }

    public boolean executeCheatCode(String code) {
        CheatCode cheat = cheatCodes.get(code.toUpperCase());
        if (cheat != null) {
            try {
                cheat.execute();
                Gdx.app.log("CheatCode", "Executed cheat: " + cheat.getName());
                return true;
            } catch (Exception e) {
                Gdx.app.error("CheatCode", "Error executing cheat " + cheat.getName() + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    private void reduceTime() {
        if (gameController == null) return;

        // Reduce remaining time by 1 minute (60 seconds)
        // This works by adding 60 seconds to the current game time
        gameController.addGameTime(60f);

        Gdx.app.log("CheatCode", "Time reduced by 1 minute");
        GameAssetManager.getGameAssetManager().playObtain();
    }

    private void levelUpPlayer() {
        if (player == null) return;

        // Calculate XP needed for next level
        int currentLevel = player.getLevel();
        int xpNeeded = 20 * (currentLevel + 1);

        // Add enough XP to trigger level up
        player.addXP(xpNeeded);

        Gdx.app.log("CheatCode", "Player leveled up from " + currentLevel + " to " + player.getLevel());
        GameAssetManager.getGameAssetManager().playLevelUp();
    }

    private void healPlayer() {
        if (player == null) return;

        // Only heal if player health is empty (0 or very low)
        if (player.getPlayerHealth() <= 0) {
            player.setPlayerHealth(player.getMaxHealth());
            Gdx.app.log("CheatCode", "Player healed to full health");
            GameAssetManager.getGameAssetManager().playObtain();
        } else {
            Gdx.app.log("CheatCode", "Heal cheat failed: Player health is not empty");
        }
    }

    private void triggerBossFight() {
        if (gameController == null) return;

        // Spawn multiple strong enemies to simulate a boss fight
        Gdx.app.log("CheatCode", "Boss fight triggered!");
        GameAssetManager.getGameAssetManager().playBatDeath(); // Use dramatic sound

        // In a real implementation, you'd spawn boss enemies through the EnemyController
        // gameController.getEnemyController().spawnBossEnemies();
    }

    private void activateGodMode() {
        if (player == null) return;

        // Make player invincible for 30 seconds
        player.setInvincible(true, 30.0f);
        Gdx.app.log("CheatCode", "God mode activated for 30 seconds");
        GameAssetManager.getGameAssetManager().playObtain();
    }

    public Map<String, CheatCode> getAllCheatCodes() {
        return new HashMap<>(cheatCodes);
    }

    public static class CheatCode {
        private final String code;
        private final String name;
        private final String description;
        private final Runnable action;

        public CheatCode(String code, String name, String description, Runnable action) {
            this.code = code;
            this.name = name;
            this.description = description;
            this.action = action;
        }

        public void execute() {
            action.run();
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
