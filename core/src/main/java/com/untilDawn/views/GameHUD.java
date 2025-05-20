package com.untilDawn.views;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;
import com.untilDawn.controllers.PlayerController;
import com.untilDawn.controllers.WeaponController;
import com.untilDawn.models.Player;
import com.untilDawn.models.utils.GameAssetManager;

public class GameHUD {
    private GameController gameController;
    private OrthographicCamera camera;

    // Reload bar properties
    private Texture reloadBarBg;
    private Texture reloadBarFill;
    private float reloadBarWidth = 40f;
    private float reloadBarHeight = 8f;
    private float reloadBarOffsetY = 50f; // Distance above player head

    public GameHUD(GameController gameController, OrthographicCamera camera) {
        this.gameController = gameController;
        this.camera = camera;

        reloadBarBg = GameAssetManager.getGameAssetManager().getReloadBarBg();
        reloadBarFill = GameAssetManager.getGameAssetManager().getReloadBarFill();
    }

    public void render() {
        SpriteBatch batch = Main.getBatch();
        WeaponController weaponController = gameController.getWeaponController();
        PlayerController playerController = gameController.getPlayerController();

        if (weaponController != null && weaponController.isReloading()) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            Player player = playerController.getPlayer();
            float playerX = player.getPosX();
            float playerY = player.getPosY();

            float barX = playerX - reloadBarWidth / 2;
            float barY = playerY + reloadBarOffsetY;

            batch.draw(reloadBarBg, barX, barY, reloadBarWidth, reloadBarHeight);

            float progress = weaponController.getReloadProgress();
            batch.draw(reloadBarFill, barX, barY, reloadBarWidth * progress, reloadBarHeight);

            batch.end();
        }


        drawAmmoCounter();
        drawHealthBar();
    }

    private void drawAmmoCounter() {
        // TODO
    }

    private void drawHealthBar() {
        //TODO
    }

    public void setReloadBarDimensions(float width, float height, float offsetY) {
        this.reloadBarWidth = width;
        this.reloadBarHeight = height;
        this.reloadBarOffsetY = offsetY;
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
