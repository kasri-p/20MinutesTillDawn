package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Enemy;
import com.untilDawn.models.Game;
import com.untilDawn.models.User;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.main.EndGameScreen;
import com.untilDawn.views.main.GameView;

public class GameController {
    private GameView view;
    private PlayerController playerController;
    private WeaponController weaponController;
    private WorldController worldController;
    private EnemyController enemyController;

    private float mapWidth;
    private float mapHeight;

    private float gameTime = 0;
    private boolean gameOver = false;
    private int timeLimit;

    public GameController(GameView view) {
        this.view = view;

        Texture mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();
        mapTexture.dispose();

        this.timeLimit = App.getGame() != null ? App.getGame().getTimeLimit() : 5;

        assert App.getGame() != null;
        this.playerController = new PlayerController(App.getGame().getPlayer());
        this.weaponController = new WeaponController(App.getGame().getSelectedWeapon());
        this.weaponController.setPlayerController(playerController);
        this.weaponController.setCamera(view.getCamera());

        this.worldController = new WorldController(playerController);
        playerController.setWeaponController(weaponController);

        // Initialize enemy controller AFTER all other controllers are set up
        this.enemyController = new EnemyController(playerController, weaponController, mapWidth, mapHeight);

        // Set up initial game state
        initializeGameState();
    }

    private void initializeGameState() {
        Game currentGame = App.getGame();

        if (currentGame != null && currentGame.getGameTime() > 0) {
            // This is a loaded game, restore the saved state
            this.gameTime = currentGame.getGameTime();

            // Player position should already be set from the loaded player object
            Gdx.app.log("GameController", "Restoring saved game - Time: " + gameTime +
                ", Player pos: (" + playerController.getPlayer().getPosX() +
                ", " + playerController.getPlayer().getPosY() + ")");
        } else {
            // New game - set player to center
            this.playerController.getPlayer().setPosX(mapWidth / 2);
            this.playerController.getPlayer().setPosY(mapHeight / 2);
            Gdx.app.log("GameController", "Starting new game");
        }
    }

    public void updateGame() {
        if (view != null && !gameOver) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            gameTime += deltaTime;

            // Update game time in the current game object for saving
            if (App.getGame() != null) {
                App.getGame().setGameTime(gameTime);
            }

            OrthographicCamera camera = view.getCamera();
            Main.getBatch().setProjectionMatrix(camera.combined);

            worldController.update();
            enemyController.update(deltaTime);
            playerController.update();
            weaponController.update();

            checkGameOver();
        }
    }

    private void checkGameOver() {
        User user = App.getLoggedInUser();
        if (playerController.getPlayer().getPlayerHealth() <= 0) {
            gameOver = true;
            App.getLoggedInUser().setDeaths(App.getLoggedInUser().getDeaths() + 1);
            if (!user.isGuest()) {
                user.setKills(user.getKills() + App.getGame().getPlayer().getKills());
                user.setSurvivalTime(user.getSurvivalTime() + gameTime);
            }
            Gdx.app.log("GameController", "Player died - Health: " + playerController.getPlayer().getPlayerHealth());
            Main.getMain().setScreen(new EndGameScreen(GameAssetManager.getGameAssetManager().getSkin(), user, gameTime, App.getGame().getPlayer().getKills(), EndGameScreen.EndGameStatus.DEFEAT));
        }

        int timeLimitSeconds = timeLimit * 60;
        if (gameTime >= timeLimitSeconds) {
            gameOver = true;
            if (!user.isGuest()) {
                user.setKills(user.getKills() + App.getGame().getPlayer().getKills());
                user.setSurvivalTime(user.getSurvivalTime() + gameTime);
            }
            Main.getMain().setScreen(new EndGameScreen(GameAssetManager.getGameAssetManager().getSkin(), user, gameTime, App.getGame().getPlayer().getKills(), EndGameScreen.EndGameStatus.VICTORY));
        }
    }

    public float getGameTime() {
        return gameTime;
    }

    public void setGameTime(float newGameTime) {
        this.gameTime = newGameTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public float getRemainingTime() {
        return Math.max(0, (timeLimit * 60) - gameTime);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public float getGameProgress() {
        return Math.min(1.0f, gameTime / (timeLimit * 60));
    }

    public String getFormattedRemainingTime() {
        float remainingTime = getRemainingTime();
        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isTimeRunningLow() {
        return getRemainingTime() <= 60;
    }

    public boolean isTimeCritical() {
        return getRemainingTime() <= 10;
    }

    public WeaponController getWeaponController() {
        return weaponController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public EnemyController getEnemyController() {
        return enemyController;
    }

    public void dispose() {
        if (enemyController != null) {
            enemyController.dispose();
        }
        playerController.getPlayer().dispose();
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public void reduceGameTime(float seconds) {
        gameTime += seconds;
        Gdx.app.log("GameController", "Game time reduced by " + seconds + " seconds");
    }

    public void addGameTime(float seconds) {
        gameTime += seconds;
        if (seconds > 0) {
            Gdx.app.log("GameController", "Game time increased by " + seconds + " seconds");
        } else {
            Gdx.app.log("GameController", "Game time reduced by " + Math.abs(seconds) + " seconds");
        }
    }

    public void saveGame() {
        if (gameOver) {
            return; // Don't save if game is over
        }

        Game currentGame = App.getGame();
        if (currentGame != null) {
            // Update game time
            currentGame.setGameTime(gameTime);

            // Save player state
            if (playerController != null && playerController.getPlayer() != null) {
                currentGame.getPlayer().setPosX(playerController.getPlayer().getPosX());
                currentGame.getPlayer().setPosY(playerController.getPlayer().getPosY());
                currentGame.getPlayer().setPlayerHealth(playerController.getPlayer().getPlayerHealth());
                currentGame.getPlayer().setMaxHealth(playerController.getPlayer().getMaxHealth());
                // Copy other player attributes that might have changed
                currentGame.getPlayer().addXP(0); // This will update level if needed
            }

            // Save weapon state
            if (weaponController != null && weaponController.getWeapon() != null) {
                currentGame.getSelectedWeapon().setAmmo(weaponController.getWeapon().getAmmo());
            }

            // Clear and save current enemies
            currentGame.getEnemies().clear();
            if (enemyController != null && enemyController.getEnemies() != null) {
                for (Enemy enemy : enemyController.getEnemies()) {
                    if (enemy.isActive()) {
                        // Create a copy of the enemy for saving
                        currentGame.addEnemy(createEnemyCopy(enemy));
                    }
                }
            }

            // Save using GameSaveSystem
            boolean success = com.untilDawn.models.utils.GameSaveSystem.saveGame(
                App.getLoggedInUser(),
                currentGame,
                currentGame.getPlayer(),
                gameTime
            );

            if (success) {
                Gdx.app.log("GameController", "Game saved successfully - Enemies: " +
                    currentGame.getEnemies().size() + ", Time: " + gameTime);
            } else {
                Gdx.app.error("GameController", "Failed to save game");
            }
        }
    }

    private Enemy createEnemyCopy(Enemy original) {
        try {
            Enemy copy;

            // Create appropriate enemy type
            if (original instanceof com.untilDawn.models.ElderBoss) {
                copy = new com.untilDawn.models.ElderBoss(
                    original.getPosX(),
                    original.getPosY(),
                    mapWidth,
                    mapHeight
                );
            } else {
                copy = new Enemy(original.getType(), original.getPosX(), original.getPosY());
            }

            // Apply damage to match current health
            int healthDiff = original.getType().getHealth() - original.getHealth();
            for (int i = 0; i < healthDiff && copy.isActive(); i++) {
                copy.hit(1);
            }

            return copy;

        } catch (Exception e) {
            Gdx.app.error("GameController", "Failed to create enemy copy: " + e.getMessage());
            return null;
        }
    }

    public boolean loadGame() {
        // Load the game save data using GameSaveSystem
        com.untilDawn.models.utils.GameSaveSystem.GameSaveData saveData =
            com.untilDawn.models.utils.GameSaveSystem.loadGame(App.getLoggedInUser());

        if (saveData == null) {
            Gdx.app.log("GameController", "No saved game found");
            return false;
        }

        // Restore the game from the save data
        Game loadedGame = com.untilDawn.models.utils.GameSaveSystem.restoreGameFromSave(saveData);
        if (loadedGame == null) {
            Gdx.app.error("GameController", "Failed to restore game from save data");
            return false;
        }

        // Set the loaded game as the current game
        App.setGame(loadedGame);

        // Update controllers with loaded state
        this.gameTime = loadedGame.getGameTime();

        // Update player
        if (playerController != null && loadedGame.getPlayer() != null) {
            playerController.getPlayer().setPosX(loadedGame.getPlayer().getPosX());
            playerController.getPlayer().setPosY(loadedGame.getPlayer().getPosY());
            playerController.getPlayer().setPlayerHealth(loadedGame.getPlayer().getPlayerHealth());
            playerController.getPlayer().setMaxHealth(loadedGame.getPlayer().getMaxHealth());
        }

        // Note: Enemy restoration is handled in EnemyController constructor

        Gdx.app.log("GameController", "Game loaded successfully");
        return true;
    }

    public void deleteSavedGame() {
        boolean success = com.untilDawn.models.utils.GameSaveSystem.deleteSavedGame(App.getLoggedInUser());
        if (success) {
            Gdx.app.log("GameController", "Saved game deleted");
        } else {
            Gdx.app.error("GameController", "Failed to delete saved game");
        }
    }
}
