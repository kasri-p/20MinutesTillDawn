package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.LevelBar;
import com.untilDawn.models.Player;
import com.untilDawn.models.utils.GameAssetManager;

// This class is responsible for rendering the HUD (Heads-Up Display) elements of the game.
public class GameHUD {
    private static final float LEVEL_BAR_HEIGHT = 30f;
    private GameController gameController;
    private OrthographicCamera camera;
    private Texture reloadBarBg;
    private Texture reloadBarFill;
    private LevelBar levelBar;
    private BitmapFont font;
    private float screenWidth;
    private float screenHeight;
    private Animation<Texture> heartAnimation;
    private float animationTime = 0f;

    public GameHUD(GameController gameController, OrthographicCamera camera) {
        this.gameController = gameController;
        this.camera = camera;

        reloadBarBg = GameAssetManager.getGameAssetManager().getReloadBarBg();
        reloadBarFill = GameAssetManager.getGameAssetManager().getReloadBarFill();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        font = new BitmapFont();
        font.getData().setScale(1.2f);

        levelBar = new LevelBar(font, screenWidth, LEVEL_BAR_HEIGHT);
        heartAnimation = GameAssetManager.getGameAssetManager().getHeartAnimation();
    }

    public void render() {
        SpriteBatch batch = Main.getBatch();
        animationTime += Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(batch.getProjectionMatrix());

        batch.setProjectionMatrix(camera.combined.cpy().setToOrtho2D(0, 0, screenWidth, screenHeight));
        batch.begin();

        Player player = gameController.getPlayerController().getPlayer();
        levelBar.update(player.getXP());
        levelBar.render(batch, screenWidth);

        drawHealthBar(batch, player);
        drawAmmoCounter(batch);
        
        batch.end();

        batch.setProjectionMatrix(camera.combined);
    }

    private void drawAmmoCounter(SpriteBatch batch) {
        int ammoCount = gameController.getWeaponController().getWeapon().getAmmo();
        int ammoMax = gameController.getWeaponController().getWeapon().getWeapon().getAmmoMax();

        String ammoText = ammoCount + " / " + ammoMax;
        float x = screenWidth - 150;
        float y = 60;

        font.setColor(Color.WHITE);
        font.draw(batch, "AMMO", x, y + 20);
        font.setColor(ammoCount > 0 ? Color.WHITE : Color.RED);
        font.draw(batch, ammoText, x, y);
    }

    private void drawHealthBar(SpriteBatch batch, Player player) {
        int currentHealth = player.getPlayerHealth();
        int maxHealth = player.getMaxHealth();

        float heartSize = 32f;
        float heartSpacing = heartSize + 5f;
        float startX = 20f;
        float startY = screenHeight - 60f;

        for (int i = 0; i < maxHealth; i++) {
            float heartX = startX + i * heartSpacing;
            float heartY = startY;

            Texture heartFrame = heartAnimation.getKeyFrame(animationTime, true);

            if (i < currentHealth) {

            } else {
                batch.setColor(0.3f, 0.3f, 0.3f, 0.8f);
            }

            batch.draw(heartFrame, heartX, heartY, heartSize, heartSize);
        }

        batch.setColor(Color.WHITE);
    }

    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
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
        if (levelBar != null) {
            levelBar.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
