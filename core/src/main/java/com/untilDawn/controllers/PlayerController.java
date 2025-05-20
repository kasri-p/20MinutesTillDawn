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
    private float screenCenterX;
    private float screenCenterY;
    private Animation<Texture> currentAnimation;
    private float stateTime = 0;
    private boolean isMoving = false;

    private WeaponController weaponController;

    private Texture mapTexture;
    private float mapWidth;
    private float mapHeight;

    public PlayerController(Player player) {
        this.player = player;
        updateScreenCenter();

        this.mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();

        player.getPlayerSprite().setPosition(
            player.getPosX() - player.getPlayerSprite().getWidth() / 2,
            player.getPosY() - player.getPlayerSprite().getHeight() / 2
        );

        updateAnimation();

        player.getPlayerSprite().draw(Main.getBatch());

        handlePlayerInput();
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

    public void idleAnimation() {
        Animation<Texture> idleAnimation = GameAssetManager.getGameAssetManager().getPlayerIdleAnimation();
        if (idleAnimation != null) {
            idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
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
