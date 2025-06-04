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

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.controller = new GameController(this);

        if (App.getGame() != null && App.getGame().getGameTime() > 0) {
            camera.position.set(
                controller.getPlayerController().getPlayer().getPosX(),
                controller.getPlayerController().getPlayer().getPosY(),
                0
            );
        } else {
            camera.position.set(
                controller.getMapWidth() / 2,
                controller.getMapHeight() / 2,
                0
            );
        }

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
            this::resumeGame
        );

        levelUpWindow.setFillParent(true);
        stage.addActor(levelUpWindow);
        levelUpWindow.toFront();

        controller.getPlayerController().getPlayer().setLevelUpWindowShown();
        Gdx.input.setInputProcessor(stage);
    }

    private void showPauseMenu() {
        gameIsPaused = true;

        pauseMenuWindow = new PauseMenuWindow(
            skin,
            controller.getPlayerController().getPlayer(),
            controller,
            stage,
            this::resumeGame,
            this::giveUpGame,
            this::saveAndExitGame
        );

        // This ensures the pause menu fills the screen and centers its content
        pauseMenuWindow.setFillParent(true);
        stage.addActor(pauseMenuWindow);
        pauseMenuWindow.toFront();

        Main.getBatch().setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(stage);
    }

    private void resumeGame() {
        gameIsPaused = false;

        if (levelUpWindow != null) {
            levelUpWindow.dispose();
            levelUpWindow = null;
        }

        if (pauseMenuWindow != null) {
            pauseMenuWindow.remove();
            pauseMenuWindow = null;
        }

        Gdx.input.setInputProcessor(this);
    }

    private void giveUpGame() {
        gameIsPaused = false;

        if (App.getLoggedInUser() != null) {
            App.getLoggedInUser().setDeaths(App.getLoggedInUser().getDeaths() + 1);
            App.save();
        }

        EndGameScreen endGameScreen = new EndGameScreen(
            GameAssetManager.getGameAssetManager().getSkin(),
            App.getLoggedInUser(),
            controller.getGameTime(),
            controller.getPlayerController().getPlayer().getKills(),
            EndGameScreen.EndGameStatus.GIVE_UP
        );
        Main.getMain().setScreen(endGameScreen);
    }

    private void saveAndExitGame() {
        if (App.getLoggedInUser() != null && !App.getLoggedInUser().isGuest()) {
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

        App.save();

        MainMenu mainMenu = new MainMenu(GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(mainMenu);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        gameHUD.resize(width, height);
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
        if (!gameIsPaused) {
            if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                showPauseMenu();
                return true;
            }

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
