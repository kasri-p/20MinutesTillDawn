package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.views.main.GameView;
import com.untilDawn.views.main.MainMenu;

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

    public GameController(GameView view) {
        this.view = view;

        // Load map dimensions
        Texture mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();
        mapTexture.dispose();

        // Initialize controllers
        this.playerController = new PlayerController(App.getGame().getPlayer());
        this.weaponController = new WeaponController(App.getGame().getSelectedWeapon());
        this.weaponController.setPlayerController(playerController);
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
        if (playerController.getPlayer().getPlayerHealth() <= 0) {
            gameOver = true;
            App.getLoggedInUser().setDeaths(App.getLoggedInUser().getDeaths() + 1);

            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(2000); // 2-second delay
                    Main.getMain().setScreen(new MainMenu(GameAssetManager.getGameAssetManager().getSkin()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        int timeLimit = App.getGame().getTimeLimit() * 60;
        if (gameTime >= timeLimit) {
            gameOver = true;

            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(2000); // 2-second delay
                    Main.getMain().setScreen(new MainMenu(GameAssetManager.getGameAssetManager().getSkin()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
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

    public float getGameTime() {
        return gameTime;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }
}
