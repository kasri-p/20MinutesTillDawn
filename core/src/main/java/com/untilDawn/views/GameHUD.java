package com.untilDawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.untilDawn.Main;
import com.untilDawn.controllers.GameController;
import com.untilDawn.models.ElderBoss;
import com.untilDawn.models.LevelBar;
import com.untilDawn.models.Player;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.utils.GameAssetManager;

public class GameHUD {
    private static final float LEVEL_BAR_HEIGHT = 30f;
    private static final float ABILITY_ICON_SIZE = 32f;
    private static final float ABILITY_BAR_HEIGHT = 50f;

    private GameController gameController;
    private OrthographicCamera camera;
    private Texture reloadBarBg;
    private Texture reloadBarFill;
    private LevelBar levelBar;
    private BitmapFont font;
    private BitmapFont smallFont;
    private float screenWidth;
    private float screenHeight;
    private Animation<Texture> heartAnimation;
    private float animationTime = 0f;

    // hey
    private BitmapFont ammoFont;
    private Texture ammoTexture;
    private BitmapFont zombieKillFont;
    private Texture zombieKillTexture;

    public GameHUD(GameController gameController, OrthographicCamera camera) {
        this.gameController = gameController;
        this.camera = camera;

        reloadBarBg = GameAssetManager.getGameAssetManager().getReloadBarBg();
        reloadBarFill = GameAssetManager.getGameAssetManager().getReloadBarFill();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        font = new BitmapFont();
        font.getData().setScale(1.2f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(0.8f);

        ammoFont = new BitmapFont();
        ammoFont.getData().setScale(1.5f);

        zombieKillFont = new BitmapFont();
        zombieKillFont.getData().setScale(1.5f);

        BitmapFont levelBarFont = GameAssetManager.getGameAssetManager().getChevyRayFont();
        levelBar = new LevelBar(levelBarFont, screenWidth, LEVEL_BAR_HEIGHT);
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
        drawGameTimer(batch);
        drawAbilityStatus(batch);

        drawBarrierLine();

        batch.end();
        renderHealthAndAmmoUI();

        batch.setProjectionMatrix(camera.combined);
    }

    private void drawBarrierLine() {
        if (gameController.getEnemyController().getElderBoss() != null &&
            gameController.getEnemyController().getElderBoss().isBarrierActive()) {

            ElderBoss elderBoss = gameController.getEnemyController().getElderBoss();
            ElderBoss.ElectricBarrier barrier = elderBoss.getBarrier();

            if (barrier != null) {
                ShapeRenderer shapeRenderer = new ShapeRenderer();

                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

                shapeRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);

                float mapWidth = gameController.getMapWidth();
                float mapHeight = gameController.getMapHeight();

                float currentWidth = barrier.getCurrentWidth();
                float currentHeight = barrier.getCurrentHeight();

                float barrierCenterX = mapWidth / 2;
                float barrierCenterY = mapHeight / 2;

                float barrierLeft = barrierCenterX - currentWidth / 2;
                float barrierRight = barrierCenterX + currentWidth / 2;
                float barrierBottom = barrierCenterY - currentHeight / 2;
                float barrierTop = barrierCenterY + currentHeight / 2;

                float camLeft = camera.position.x - camera.viewportWidth / 2;
                float camRight = camera.position.x + camera.viewportWidth / 2;
                float camBottom = camera.position.y - camera.viewportHeight / 2;
                float camTop = camera.position.y + camera.viewportHeight / 2;

                float thickness = 4f;

                if (barrierLeft >= camLeft - thickness && barrierLeft <= camRight + thickness) {
                    float visibleTop = Math.min(barrierTop, camTop);
                    float visibleBottom = Math.max(barrierBottom, camBottom);
                    if (visibleTop > visibleBottom) {
                        shapeRenderer.rectLine(barrierLeft, visibleBottom, barrierLeft, visibleTop, thickness);
                    }
                }

                if (barrierRight >= camLeft - thickness && barrierRight <= camRight + thickness) {
                    float visibleTop = Math.min(barrierTop, camTop);
                    float visibleBottom = Math.max(barrierBottom, camBottom);
                    if (visibleTop > visibleBottom) {
                        shapeRenderer.rectLine(barrierRight, visibleBottom, barrierRight, visibleTop, thickness);
                    }
                }

                if (barrierBottom >= camBottom - thickness && barrierBottom <= camTop + thickness) {
                    float visibleLeft = Math.max(barrierLeft, camLeft);
                    float visibleRight = Math.min(barrierRight, camRight);
                    if (visibleRight > visibleLeft) {
                        shapeRenderer.rectLine(visibleLeft, barrierBottom, visibleRight, barrierBottom, thickness);
                    }
                }

                if (barrierTop >= camBottom - thickness && barrierTop <= camTop + thickness) {
                    float visibleLeft = Math.max(barrierLeft, camLeft);
                    float visibleRight = Math.min(barrierRight, camRight);
                    if (visibleRight > visibleLeft) {
                        shapeRenderer.rectLine(visibleLeft, barrierTop, visibleRight, barrierTop, thickness);
                    }
                }

                float cornerSize = 20f;

                if (barrierLeft >= camLeft - cornerSize && barrierLeft <= camRight + cornerSize &&
                    barrierTop >= camBottom - cornerSize && barrierTop <= camTop + cornerSize) {
                    shapeRenderer.rectLine(barrierLeft - cornerSize, barrierTop, barrierLeft + cornerSize, barrierTop, thickness);
                    shapeRenderer.rectLine(barrierLeft, barrierTop - cornerSize, barrierLeft, barrierTop + cornerSize, thickness);
                }

                if (barrierRight >= camLeft - cornerSize && barrierRight <= camRight + cornerSize &&
                    barrierTop >= camBottom - cornerSize && barrierTop <= camTop + cornerSize) {
                    shapeRenderer.rectLine(barrierRight - cornerSize, barrierTop, barrierRight + cornerSize, barrierTop, thickness);
                    shapeRenderer.rectLine(barrierRight, barrierTop - cornerSize, barrierRight, barrierTop + cornerSize, thickness);
                }

                if (barrierLeft >= camLeft - cornerSize && barrierLeft <= camRight + cornerSize &&
                    barrierBottom >= camBottom - cornerSize && barrierBottom <= camTop + cornerSize) {
                    shapeRenderer.rectLine(barrierLeft - cornerSize, barrierBottom, barrierLeft + cornerSize, barrierBottom, thickness);
                    shapeRenderer.rectLine(barrierLeft, barrierBottom - cornerSize, barrierLeft, barrierBottom + cornerSize, thickness);
                }

                if (barrierRight >= camLeft - cornerSize && barrierRight <= camRight + cornerSize &&
                    barrierBottom >= camBottom - cornerSize && barrierBottom <= camTop + cornerSize) {
                    shapeRenderer.rectLine(barrierRight - cornerSize, barrierBottom, barrierRight + cornerSize, barrierBottom, thickness);
                    shapeRenderer.rectLine(barrierRight, barrierBottom - cornerSize, barrierRight, barrierBottom + cornerSize, thickness);
                }

                shapeRenderer.end();
                shapeRenderer.dispose();
            }
        }
    }


    private void drawGameTimer(SpriteBatch batch) {
        float gameTime = gameController.getGameTime();
        int timeLimit = gameController.getTimeLimit();

        float remainingTime = Math.max(0, (timeLimit * 60) - gameTime);

        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);

        String timeText = String.format("%02d:%02d", minutes, seconds);

        float x = screenWidth - 100;
        float y = screenHeight - 30;
        BitmapFont chevyFont = GameAssetManager.getGameAssetManager().getChevyRayFont();
        float bigScale = 2.5f;
        chevyFont.setColor(Color.WHITE);
        chevyFont.draw(batch, timeText, x, y);

        float surviveY = y - 40;

        if (remainingTime <= 10 && remainingTime > 0) {
            float pulse = 1.0f + 0.3f * (float) Math.sin(animationTime * 8);
            chevyFont.draw(batch, timeText, x, y);
        }
    }

    private void drawHealthBar(SpriteBatch batch, Player player) {
        int currentHealth = player.getPlayerHealth();
        int maxHealth = player.getMaxHealth();

        float heartSize = 34f;
        float heartSpacing = heartSize + 5f;
        float startX = 20f;
        float startY = screenHeight - 60f;

        for (int i = 0; i < maxHealth; i++) {
            float heartX = startX + i * heartSpacing;
            float heartY = startY;

            Texture heartFrame = heartAnimation.getKeyFrame(animationTime, true);

            if (i < currentHealth) {
                batch.setColor(Color.WHITE);
            } else {
                batch.setColor(0.3f, 0.3f, 0.3f, 0.8f);
            }

            batch.draw(heartFrame, heartX, heartY, heartSize, heartSize);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawAbilityStatus(SpriteBatch batch) {
        float startX = 20f;
        float startY = screenHeight - 120f;
        float iconSpacing = ABILITY_ICON_SIZE + 10f;
        int iconIndex = 0;

        Abilities[] activeAbilities = {Abilities.DAMAGER, Abilities.SPEEDY, Abilities.SHIELD, Abilities.MULTISHOT};

        for (Abilities ability : activeAbilities) {
            float iconX = startX + iconIndex * iconSpacing;

            drawAbilityIcon(batch, ability, iconX, startY);
            iconIndex++;
        }
    }


    private void drawAbilityIcon(SpriteBatch batch, Abilities ability, float x, float y) {
        Color bgColor;
        if (ability.isActive()) {
            bgColor = new Color(0.2f, 0.8f, 0.2f, 0.8f);
        } else if (ability.getRemainingCooldown() > 0) {
            bgColor = new Color(0.8f, 0.2f, 0.2f, 0.6f);
        } else {
            bgColor = new Color(0.5f, 0.5f, 0.5f, 0.6f);
        }

        batch.setColor(bgColor);
        batch.draw(reloadBarBg, x - 2, y - 2, ABILITY_ICON_SIZE + 4, ABILITY_ICON_SIZE + 4);

        Texture abilityTexture = ability.getTexture();
        if (abilityTexture != null) {
            batch.setColor(Color.WHITE);
            batch.draw(abilityTexture, x, y, ABILITY_ICON_SIZE, ABILITY_ICON_SIZE);
        } else {
            font.setColor(Color.WHITE);
            font.draw(batch, ability.getIcon(), x + 4, y + 20);
        }

        if (ability.isActive() && ability.getDuration() > 0) {
            drawAbilityProgressBar(batch, x, y - 8, ABILITY_ICON_SIZE, 4, ability.getProgress(), Color.GREEN);
        } else if (ability.getRemainingCooldown() > 0) {
            drawAbilityProgressBar(batch, x, y - 8, ABILITY_ICON_SIZE, 4, ability.getCooldownProgress(), Color.RED);
        }
        if (ability.isActive()) {
            smallFont.setColor(Color.WHITE);
            String timeText = String.format("%.1f", ability.getRemainingDuration());
            smallFont.draw(batch, timeText, x, y - 12);
        } else if (ability.getRemainingCooldown() > 0) {
            smallFont.setColor(Color.ORANGE);
            String timeText = String.format("%.1f", ability.getRemainingCooldown());
            smallFont.draw(batch, timeText, x, y - 12);
        }

        batch.setColor(Color.WHITE);
    }


    private void drawAbilityProgressBar(SpriteBatch batch, float x, float y, float width, float height, float progress, Color color) {
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(reloadBarBg, x, y, width, height);

        batch.setColor(color);
        batch.draw(reloadBarFill, x, y, width * progress, height);

        batch.setColor(Color.WHITE);
    }


    private void renderHealthAndAmmoUI() {
        Main.getBatch().begin();

        int maxAmmo = gameController.getWeaponController().getWeapon().getMaxAmmoWithBonus();
        int currentAmmo = gameController.getWeaponController().getWeapon().getAmmo();

        int killCount = gameController.getPlayerController().getPlayer().getKills();


        float ammoIconX = 20;
        float ammoIconY = screenHeight - 200;
        float ammoTextX = ammoIconX + 70;
        float ammoTextY = ammoIconY + 40;

        float zombieKillX = 30;
        float zombieKillY = ammoIconY - 60;
        float zombieKillTextX = zombieKillX + 70;
        float zombieKillTextY = zombieKillY + 40;


        Texture ammoIcon = GameAssetManager.getGameAssetManager().getAmmoIcon();
        if (ammoIcon != null) {
            float ammoScale = 2.0f;
            float ammoWidth = ammoIcon.getWidth() * ammoScale;
            float ammoHeight = ammoIcon.getHeight() * ammoScale;

            Main.getBatch().draw(ammoIcon, ammoIconX, ammoIconY, ammoWidth, ammoHeight);
        }

        if (ammoFont != null) {
            String ammoText = String.format("%03d/%03d", currentAmmo, maxAmmo);
            ammoFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            ammoFont.draw(Main.getBatch(), ammoText, ammoTextX, ammoTextY);
        }

        Texture zombieKill = GameAssetManager.getGameAssetManager().getZombieSkull();
        if (zombieKill != null) {
            float zombieScale = 4.5f;
            float zombieWidth = zombieKill.getWidth() * zombieScale;
            float zombieHeight = zombieKill.getHeight() * zombieScale;

            Main.getBatch().draw(zombieKill, zombieKillX, zombieKillY, zombieWidth, zombieHeight);
        }
        if (zombieKillFont != null) {
            String zombieText = String.valueOf(killCount);
            zombieKillFont.setColor(1.0f, 1.0f, 1.0f, 1.0f); // White color
            zombieKillFont.draw(Main.getBatch(), zombieText, zombieKillTextX, zombieKillTextY);
        }


        Main.getBatch().end();
    }


    private void drawAbilityHotkeys(SpriteBatch batch) {
        float x = screenWidth - 200;
        float y = screenHeight - 150;
        float lineHeight = 20f;

        smallFont.setColor(Color.LIGHT_GRAY);
        smallFont.draw(batch, "Ability Hotkeys:", x, y);
        smallFont.draw(batch, "1 - Damager", x, y - lineHeight);
        smallFont.draw(batch, "2 - Speedy", x, y - lineHeight * 2);
        smallFont.draw(batch, "3 - Shield", x, y - lineHeight * 3);
        smallFont.draw(batch, "4 - Multishot", x, y - lineHeight * 4);
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
        if (smallFont != null) {
            smallFont.dispose();
        }
    }
}
