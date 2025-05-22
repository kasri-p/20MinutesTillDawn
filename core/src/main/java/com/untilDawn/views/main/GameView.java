package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.App;
import com.untilDawn.models.utils.GameAssetManager;
import com.untilDawn.models.utils.GameSaveSystem;
import com.untilDawn.models.utils.GrayscaleShader;
import com.untilDawn.models.utils.LightingManager;
import com.untilDawn.views.GameHUD;
import com.untilDawn.views.StartMenu;
import com.untilDawn.views.window.LevelUpWindow;
import com.untilDawn.views.window.PauseMenuWindow;

import java.util.Map;

public class GameView implements Screen, InputProcessor {
    private Stage stage;
    private GameController controller;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private GameHUD gameHUD;
    private LightingManager lightingManager;
    private GrayscaleShader grayscaleShader;

    private Texture mapTexture;
    private float mapWidth;
    private float mapHeight;

    // Level up window
    private LevelUpWindow levelUpWindow;
    private boolean gameIsPaused = false;
    private Skin skin;

    // Pause menu
    private PauseMenuWindow pauseMenuWindow;

    public GameView(Skin skin) {
        this.skin = skin;

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.controller = new GameController(this);
        camera.position.set(controller.getPlayerController().getPlayer().getPosX(), controller.getPlayerController().getPlayer().getPosY(), 0);
        camera.update();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        this.mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();

        this.stage = new Stage(viewport);

        this.gameHUD = new GameHUD(controller, camera);

        this.lightingManager = LightingManager.getInstance();

        this.grayscaleShader = GrayscaleShader.getInstance();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (controller.getPlayerController().getPlayer().shouldShowLevelUpWindow() && !gameIsPaused) {
            showLevelUpWindow();
        }

        if (!gameIsPaused) {
            float camHalfWidth = camera.viewportWidth / 2;
            float camHalfHeight = camera.viewportHeight / 2;

            float playerX = controller.getPlayerController().getPlayer().getPosX();
            float playerY = controller.getPlayerController().getPlayer().getPosY();

            float clampedX = MathUtils.clamp(playerX, camHalfWidth, mapWidth - camHalfWidth);
            float clampedY = MathUtils.clamp(playerY, camHalfHeight, mapHeight - camHalfHeight);

            camera.position.set(clampedX, clampedY, 0);
            camera.update();

            // Apply black and white shader if enabled
            if (App.isBlackAndWhiteEnabled()) {
                grayscaleShader.enable(Main.getBatch());
            }

            Main.getBatch().setProjectionMatrix(camera.combined);
            Main.getBatch().begin();

            controller.updateGame();

            lightingManager.render(Main.getBatch(), camera, playerX, playerY);

            Main.getBatch().end();

            if (App.isBlackAndWhiteEnabled()) {
                grayscaleShader.disable(Main.getBatch());
            }

            gameHUD.render();
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void showLevelUpWindow() {
        gameIsPaused = true;

        levelUpWindow = new LevelUpWindow(
            skin,
            controller.getPlayerController().getPlayer(),
            stage,
            () -> {
                resumeGame();
            }
        );

        stage.addActor(levelUpWindow);

        controller.getPlayerController().getPlayer().setLevelUpWindowShown();

        // Change input processor to stage to handle window interactions
        Gdx.input.setInputProcessor(stage);
    }

    private void showPauseMenu() {
        gameIsPaused = true;

        pauseMenuWindow = new PauseMenuWindow(
            skin,
            controller.getPlayerController().getPlayer(),
            stage,
            () -> resumeGame(), // onResume
            () -> giveUpGame(),  // onGiveUp
            () -> saveAndExitGame() // onSaveAndExit
        );

        // Clear any existing actors that might interfere
        stage.clear();
        stage.addActor(pauseMenuWindow);

        // Force the window to be centered and on top
        pauseMenuWindow.toFront();
        pauseMenuWindow.setZIndex(1000); // Ensure it's on top

        // Change input processor to stage to handle window interactions
        Gdx.input.setInputProcessor(stage);

        // Debug: Log window position
        System.out.println("Pause menu created at: " + pauseMenuWindow.getX() + ", " + pauseMenuWindow.getY());
        System.out.println("Window size: " + pauseMenuWindow.getWidth() + "x" + pauseMenuWindow.getHeight());
    }

    private void resumeGame() {
        gameIsPaused = false;

        // Remove level up window if it exists
        if (levelUpWindow != null) {
            levelUpWindow.dispose();
            levelUpWindow = null;
        }

        // Remove pause menu if it exists
        if (pauseMenuWindow != null) {
            pauseMenuWindow.remove();
            pauseMenuWindow = null;
        }

        // Restore input processor to this GameView
        Gdx.input.setInputProcessor(this);
    }

    private void giveUpGame() {
        gameIsPaused = false;

        // Update user stats
        if (App.getLoggedInUser() != null) {
            App.getLoggedInUser().setDeaths(App.getLoggedInUser().getDeaths() + 1);
            App.save();
        }

        // Return to start menu
        StartMenu startMenu = new StartMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(startMenu);
    }

    private void saveAndExitGame() {
        if (App.getLoggedInUser() != null && !App.getLoggedInUser().isGuest()) {
            // Save the current game state
            boolean saved = GameSaveSystem.saveGame(
                App.getLoggedInUser(),
                App.getGame(),
                controller.getPlayerController().getPlayer(),
                controller.getGameTime()
            );

            if (saved) {
                Gdx.app.log("GameView", "Game saved successfully");
            } else {
                Gdx.app.error("GameView", "Failed to save game");
            }
        }

        // Save user data
        App.save();

        // Return to main menu
        MainMenu mainMenu = new MainMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(mainMenu);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        gameHUD.resize(width, height);

        // Update stage viewport as well
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (gameHUD != null) {
            gameHUD.dispose();
        }
        if (mapTexture != null) {
            mapTexture.dispose();
        }
        if (levelUpWindow != null) {
            levelUpWindow.dispose();
        }
        if (pauseMenuWindow != null) {
            pauseMenuWindow.remove();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        // Only process game input if not paused
        if (!gameIsPaused) {
            // Handle escape key to show pause menu
            if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                showPauseMenu();
                return true;
            }

            // Handle pause key from keybinds
            Map<String, String> keyBinds = App.getKeybinds();
            String pauseKey = keyBinds.get("Pause");
            if (pauseKey != null) {
                try {
                    int pauseKeyCode = com.badlogic.gdx.Input.Keys.valueOf(pauseKey);
                    if (keycode == pauseKeyCode) {
                        showPauseMenu();
                        return true;
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid key name, ignore
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Only process game input if not paused
        if (!gameIsPaused) {
            Vector3 worldCoords = new Vector3(screenX, screenY, 0);
            camera.unproject(worldCoords);

            controller.getWeaponController().handleWeaponShoot((int) worldCoords.x, (int) worldCoords.y);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!gameIsPaused) {
            Vector3 worldCoords = new Vector3(screenX, screenY, 0);
            camera.unproject(worldCoords);

            controller.getWeaponController().handleWeaponRotation((int) worldCoords.x, (int) worldCoords.y);
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public boolean isGamePaused() {
        return gameIsPaused;
    }
}
