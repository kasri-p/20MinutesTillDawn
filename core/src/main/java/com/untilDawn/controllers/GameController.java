package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.Main;
import com.untilDawn.models.App;
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
    private int timeLimit; // Store time limit for HUD display

    public GameController(GameView view) {
        this.view = view;

        // Load map dimensions
        Texture mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();
        mapTexture.dispose();

        // Get time limit from the current game
        this.timeLimit = App.getGame() != null ? App.getGame().getTimeLimit() : 5; // Default 5 minutes

        // Initialize controllers
        this.playerController = new PlayerController(App.getGame().getPlayer());
        this.weaponController = new WeaponController(App.getGame().getSelectedWeapon());
        this.weaponController.setPlayerController(playerController);

        this.weaponController.setCamera(view.getCamera());

        this.worldController = new WorldController(playerController);
        playerController.setWeaponController(weaponController);
        this.enemyController = new EnemyController(playerController, weaponController, mapWidth, mapHeight);

        // Set player starting position
        this.playerController.getPlayer().setPosX(mapWidth / 2);
        this.playerController.getPlayer().setPosY(mapHeight / 2);
    }

    public void updateGame() {
        if (view != null && !gameOver) {

            float deltaTime = Math.min(0.025f, Gdx.graphics.getDeltaTime()); // Capped at 40 fps for physics stability
            gameTime += deltaTime;

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
}
