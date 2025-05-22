package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.App;
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
    }

    public void render() {
        SpriteBatch batch = Main.getBatch();

        batch.setProjectionMatrix(batch.getProjectionMatrix());

        batch.setProjectionMatrix(camera.combined.cpy().setToOrtho2D(0, 0, screenWidth, screenHeight));
        batch.begin();

        Player player = gameController.getPlayerController().getPlayer();
        levelBar.update(player.getXP());
        levelBar.render(batch, screenWidth);

        drawAmmoCounter(batch);
        drawHealthBar(batch);

        batch.end();

        batch.setProjectionMatrix(camera.combined);
    }

    private void drawAmmoCounter(SpriteBatch batch) {
        // TODO
        int ammoCount = App.getGame().getSelectedWeapon().getAmmo();
        int ammoMax = App.getGame().getSelectedWeapon().getWeapon().getAmmoMax();
    }

    private void drawHealthBar(SpriteBatch batch) {
        //TODO
        int maxHealth = App.getGame().getPlayer().getCharacter().getHp();
        int health = maxHealth - App.getGame().getPlayer().getPlayerHealth();

           
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
