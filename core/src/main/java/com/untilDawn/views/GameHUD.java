package com.untilDawn.views;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.utils.GameAssetManager;

// This class is responsible for rendering the HUD (Heads-Up Display) elements of the game.
public class GameHUD {
    private GameController gameController;
    private OrthographicCamera camera;

    private Texture reloadBarBg;
    private Texture reloadBarFill;

    public GameHUD(GameController gameController, OrthographicCamera camera) {
        this.gameController = gameController;
        this.camera = camera;

        reloadBarBg = GameAssetManager.getGameAssetManager().getReloadBarBg();
        reloadBarFill = GameAssetManager.getGameAssetManager().getReloadBarFill();
    }

    public void render() {
        drawAmmoCounter();
        drawHealthBar();
    }

    private void drawAmmoCounter() {
        // TODO
    }

    private void drawHealthBar() {
        //TODO
    }

    public void dispose() {
        if (reloadBarBg != null) {
            reloadBarBg.dispose();
            reloadBarBg = null;
        }
        if (reloadBarFill != null) {
            reloadBarFill.dispose();
            reloadBarFill = null;
        }
    }
}
