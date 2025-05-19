package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.Main;

public class WorldController {
    private PlayerController playerController;
    private Texture backgroundTexture;
    private float backgroundX = 0;
    private float backgroundY = 0;
    private float screenWidth;
    private float screenHeight;
    private float backgroundScale = 1.0f;

    public WorldController(PlayerController playerController) {
        this.backgroundTexture = new Texture("Images/background.png");
        this.playerController = playerController;
        updateScreenDimensions();
        calculateBackgroundScale();
    }

    private void updateScreenDimensions() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    }

    private void calculateBackgroundScale() {
        // Calculate the scale to make the background cover the entire screen
        float scaleX = screenWidth / backgroundTexture.getWidth();
        float scaleY = screenHeight / backgroundTexture.getHeight();

        // Use the larger scale to ensure the background always covers the entire screen
        backgroundScale = Math.max(scaleX, scaleY);
    }

    public void update() {
        // Update player movement relative to background
        backgroundX = playerController.getPlayer().getPosX();
        backgroundY = playerController.getPlayer().getPosY();

        // Calculate the scaled dimensions
        float scaledWidth = backgroundTexture.getWidth() * backgroundScale;
        float scaledHeight = backgroundTexture.getHeight() * backgroundScale;

        // Calculate drawing position to center the background
        float posX = backgroundX - (scaledWidth - screenWidth) / 2;
        float posY = backgroundY - (scaledHeight - screenHeight) / 2;

        // Draw the background
        Main.getBatch().draw(
            backgroundTexture,
            posX, posY,
            scaledWidth, scaledHeight
        );
    }

    // Call this when the screen is resized
    public void handleResize(int width, int height) {
        updateScreenDimensions();
        calculateBackgroundScale();
    }
}
