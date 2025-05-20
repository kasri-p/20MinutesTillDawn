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
import com.untilDawn.views.GameHUD;

public class GameView implements Screen, InputProcessor {
    private Stage stage;
    private GameController controller;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private GameHUD gameHUD;

    private Texture mapTexture;
    private float mapWidth;
    private float mapHeight;

    // Add these variables to store the player's initial position
    private float initialPlayerX = 0;
    private float initialPlayerY = 0;
    private boolean cameraInitialized = false;

    public GameView(Skin skin) {
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

        // Initialize the GameHUD
        this.gameHUD = new GameHUD(controller, camera);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        float camHalfWidth = camera.viewportWidth / 2;
        float camHalfHeight = camera.viewportHeight / 2;

        float playerX = controller.getPlayerController().getPlayer().getPosX();
        float playerY = controller.getPlayerController().getPlayer().getPosY();

        float clampedX = MathUtils.clamp(playerX, camHalfWidth, mapWidth - camHalfWidth);
        float clampedY = MathUtils.clamp(playerY, camHalfHeight, mapHeight - camHalfHeight);

        camera.position.set(clampedX, clampedY, 0);
        camera.update();

        Main.getBatch().setProjectionMatrix(camera.combined);
        Main.getBatch().begin();
        controller.updateGame();
        Main.getBatch().end();

        // Render the HUD
        gameHUD.render();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
    }

    @Override
    public boolean keyDown(int keycode) {
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
        Vector3 worldCoords = new Vector3(screenX, screenY, 0);
        viewport.unproject(worldCoords);

        controller.getWeaponController().handleWeaponShoot((int) worldCoords.x, (int) worldCoords.y);

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
        Vector3 worldCoords = new Vector3(screenX, screenY, 0);
        viewport.unproject(worldCoords);

        controller.getWeaponController().handleWeaponRotation((int) worldCoords.x, (int) worldCoords.y);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
