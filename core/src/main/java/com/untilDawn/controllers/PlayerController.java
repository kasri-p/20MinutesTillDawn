package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Player;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.Map;

public class PlayerController {
    private Player player;
    private boolean recentlyFlipped = false;

    private Animation<Texture> currentAnimation;
    private float stateTime = 0;
    private boolean isMoving = false;

    private WeaponController weaponController;

    private Texture mapTexture;
    private float mapWidth;
    private float mapHeight;

    public PlayerController(Player player) {
        this.player = player;

        this.mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        player.update(deltaTime);

        player.getPlayerSprite().setPosition(
            player.getPosX() - player.getPlayerSprite().getWidth() / 2,
            player.getPosY() - player.getPlayerSprite().getHeight() / 2
        );

        updateAnimation();

        player.updateLevelUpAnimation(deltaTime);

        player.getPlayerSprite().draw(Main.getBatch());

        if (player.isLevelingUp()) {
            drawLevelUpAnimation();
        }

        handlePlayerInput();
    }

    private void drawLevelUpAnimation() {
        Texture levelUpFrame = player.getLevelUpFrame();
        if (levelUpFrame != null) {
            float animationProgress = player.getLevelUpAnimationProgress();

            float scale;
            float animationHeight;
            float animationWidth;

            if (animationProgress < 0.2f) {
                float strikeProgress = animationProgress / 0.2f;
                scale = 0.8f + strikeProgress * 0.4f;

                animationWidth = 32f * scale;

                float screenTop = player.getPosY() + 600f;
                animationHeight = screenTop - player.getPosY();

            } else if (animationProgress < 0.3f) {
                scale = 1.2f + ((animationProgress - 0.2f) / 0.1f) * 0.3f;
                animationWidth = 48f * scale;
                animationHeight = 48f * scale;

            } else {

                scale = 1.5f + ((animationProgress - 0.3f) / 0.7f) * 0.2f;
                animationWidth = 110f * scale;
                animationHeight = 55f * scale;
            }

            float centerX = player.getPosX();
            float centerY = player.getPosY();

            float animationX, animationY;

            if (animationProgress < 0.2f) {
                float screenTop = centerY + 600f;
                animationX = centerX - animationWidth / 2;
                animationY = centerY;
            } else {
                animationX = centerX - animationWidth / 2;
                animationY = centerY - animationHeight / 2;
            }

            float alpha = 1.0f;
            if (animationProgress < 0.25f) {
                alpha = 0.9f + 0.1f * (float) Math.sin(animationProgress * 25f);
            } else {
                alpha = 0.95f + 0.05f * (float) Math.sin(animationProgress * 6f);
            }

            float originalAlpha = Main.getBatch().getColor().a;
            Main.getBatch().setColor(1f, 1f, 1f, alpha);

            Main.getBatch().draw(
                levelUpFrame,
                animationX,
                animationY,
                animationWidth,
                animationHeight
            );

            Main.getBatch().setColor(1f, 1f, 1f, originalAlpha);
        }
    }

    private void updateAnimation() {
        Animation<Texture> animation = null;

        if (isMoving) {
            animation = GameAssetManager.getGameAssetManager().getPlayerRunAnimation();
            player.setPlayerIdle(false);
            player.setPlayerRunning(true);
        } else {
            animation = GameAssetManager.getGameAssetManager().getPlayerIdleAnimation();
            player.setPlayerIdle(true);
            player.setPlayerRunning(false);
        }

        if (animation != currentAnimation) {
            currentAnimation = animation;
        }

        if (currentAnimation != null) {
            Texture currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            player.getPlayerSprite().setTexture(currentFrame);
        }
    }

    public void handlePlayerInput() {
        isMoving = false;

        float newX = player.getPosX();
        float newY = player.getPosY();
        Map<String, String> keyBinds = App.getKeybinds();
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Up")))) {
            newY += player.getSpeed();
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Right")))) {
            newX += player.getSpeed();
            isMoving = true;
            if (recentlyFlipped) {
                player.getPlayerSprite().flip(true, false);
                recentlyFlipped = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Down")))) {
            newY -= player.getSpeed();
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Left")))) {
            newX -= player.getSpeed();
            isMoving = true;
            if (!recentlyFlipped) {
                player.getPlayerSprite().flip(true, false);
                recentlyFlipped = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Reload")))) {
            handleReload();
        }

        float halfWidth = player.getPlayerSprite().getWidth() / 2f;
        float halfHeight = player.getPlayerSprite().getHeight() / 2f;

        newX = MathUtils.clamp(newX, halfWidth, mapWidth - halfWidth);
        newY = MathUtils.clamp(newY, halfHeight, mapHeight - halfHeight);

        player.setPosX(newX);
        player.setPosY(newY);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void handleReload() {
        weaponController.startReload();
    }

    public void setWeaponController(WeaponController weaponController) {
        this.weaponController = weaponController;
    }
}
