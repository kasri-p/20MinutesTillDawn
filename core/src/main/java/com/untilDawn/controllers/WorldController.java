package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.Main;

public class WorldController {
    private PlayerController playerController;
    private Texture backgroundTexture;
    private float backgroundX = 0;
    private float backgroundY = 0;

    public WorldController(PlayerController playerController) {
        this.backgroundTexture = new Texture("Images/background.png");
        this.playerController = playerController;
    }

    public void update() {
        // Draw the background relative to player position
        // The player is at the center of the screen, so we offset the background
        // based on the player's position in the world

        // Calculate tile positions based on camera position
        float playerPosX = playerController.getPlayer().getPosX();
        float playerPosY = playerController.getPlayer().getPosY();

        // Get the dimensions of the background texture
        float bgWidth = backgroundTexture.getWidth();
        float bgHeight = backgroundTexture.getHeight();

        // Calculate offsets for tiling
        float offsetX = (playerPosX % bgWidth);
        float offsetY = (playerPosY % bgHeight);

        // Calculate the starting position for the first tile
        float startX = playerPosX - offsetX - bgWidth;
        float startY = playerPosY - offsetY - bgHeight;

        // Calculate how many tiles needed to cover the viewport
        int tilesX = (int) Math.ceil(Gdx.graphics.getWidth() / bgWidth) + 2;
        int tilesY = (int) Math.ceil(Gdx.graphics.getHeight() / bgHeight) + 2;

        // Draw the background tiles
        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                float tileX = startX + (x * bgWidth);
                float tileY = startY + (y * bgHeight);
                Main.getBatch().draw(backgroundTexture, tileX, tileY);
            }
        }
    }
}
