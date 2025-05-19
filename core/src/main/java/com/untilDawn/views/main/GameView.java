package com.untilDawn.views.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;

public class GameView implements Screen, InputProcessor {
    // Define game world size - adjust these values based on your game's needs
    private final float GAME_WIDTH = 800;
    private final float GAME_HEIGHT = 600;
    private Stage stage;
    private GameController controller;
    private OrthographicCamera camera;
    private FitViewport viewport;

    public GameView(Skin skin) {
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

        // Use FitViewport to maintain aspect ratio
        viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);

        this.stage = new Stage(viewport);
        this.controller = new GameController(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Update camera position to follow player
        updateCamera();

        // Apply camera
        camera.update();
        Main.getBatch().setProjectionMatrix(camera.combined);

        Main.getBatch().begin();
        controller.updateGame();
        Main.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void updateCamera() {
        // Center camera on player
        if (controller.getPlayerController() != null && controller.getPlayerController().getPlayer() != null) {
            // Get player position
            float playerX = controller.getPlayerController().getPlayer().getPosX();
            float playerY = controller.getPlayerController().getPlayer().getPosY();

            // Center camera on player
            camera.position.set(playerX, playerY, 0);
        }
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
        // Convert screen coordinates to world coordinates
        float worldX = screenX;
        float worldY = screenY;
        if (viewport != null) {
            viewport.unproject(Gdx.input.getX(), Gdx.input.getY());
            worldX = Gdx.input.getX();
            worldY = Gdx.input.getY();
        }

        controller.getWeaponController().handleWeaponShoot((int) worldX, (int) worldY);
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
        float worldX = screenX;
        float worldY = screenY;
        if (viewport != null) {
            viewport.unproject(Gdx.input.getX(), Gdx.input.getY());
            worldX = Gdx.input.getX();
            worldY = Gdx.input.getY();
        }

        controller.getWeaponController().handleWeaponRotation((int) worldX, (int) worldY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    // Add getter for camera
    public OrthographicCamera getCamera() {
        return camera;
    }
}
