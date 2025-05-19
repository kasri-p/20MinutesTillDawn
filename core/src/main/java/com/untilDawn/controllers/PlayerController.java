package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.untilDawn.Main;
import com.untilDawn.models.Player;

public class PlayerController {
    private Player player;
    private boolean recentlyFlipped = false;
    private float screenCenterX;
    private float screenCenterY;

    // Speed scaling factor for different screen sizes
    private float speedScaleFactor = 1.0f;

    public PlayerController(Player player) {
        this.player = player;
        updateScreenCenter();
        centerPlayerSprite();
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;

        // Adjust speed scaling based on screen size
        speedScaleFactor = Math.min(
            Gdx.graphics.getWidth() / 1920f,
            Gdx.graphics.getHeight() / 1080f
        );
        if (speedScaleFactor < 0.1f) speedScaleFactor = 0.1f;
    }

    private void centerPlayerSprite() {
        if (player != null && player.getPlayerSprite() != null) {
            Sprite sprite = player.getPlayerSprite();
            sprite.setPosition(
                screenCenterX - sprite.getWidth() / 2,
                screenCenterY - sprite.getHeight() / 2
            );
        }
    }

    public void update() {
        // Update screen center values
        updateScreenCenter();

        // Draw the player sprite
        player.getPlayerSprite().draw(Main.getBatch());

        // Handle animations
        if (player.isPlayerIdle()) {
            idleAnimation();
        }

        // Handle input
        handlePlayerInput();
    }

    public void handlePlayerInput() {
        float effectiveSpeed = player.getSpeed() * speedScaleFactor;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setPosY(player.getPosY() - effectiveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setPosX(player.getPosX() - effectiveSpeed);
            if (recentlyFlipped) {
                player.getPlayerSprite().flip(true, false);
                recentlyFlipped = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setPosY(player.getPosY() + effectiveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setPosX(player.getPosX() + effectiveSpeed);
            if (!recentlyFlipped) {
                player.getPlayerSprite().flip(true, false);
                recentlyFlipped = true;
            }
        }

        // Make sure player sprite stays centered on screen
        centerPlayerSprite();
    }

    public void idleAnimation() {
        Array<Texture> frames = new Array<>();

        for (int i = 0; i < 4; i++) {
            String framePath = "Images/characters/" + "Abby" + "/run" + i + ".png";

            if (Gdx.files.internal(framePath).exists()) {
                Texture frameTex = new Texture(Gdx.files.internal(framePath));
                frames.add(frameTex);
            }
        }

        Animation<Texture> animation = new Animation<>(0.1f, frames);

        player.getPlayerSprite().setRegion(animation.getKeyFrame(player.getTime()));

        if (!animation.isAnimationFinished(player.getTime())) {
            player.setTime(player.getTime() + Gdx.graphics.getDeltaTime());
        } else {
            player.setTime(0);
        }

        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    // Call this method when the screen is resized
    public void handleResize(int width, int height) {
        updateScreenCenter();
        centerPlayerSprite();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        centerPlayerSprite();
    }
}
