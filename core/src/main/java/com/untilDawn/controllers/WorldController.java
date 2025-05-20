package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.untilDawn.Main;

public class WorldController {
    private PlayerController playerController;
    private Texture backgroundTexture;


    public WorldController(PlayerController playerController) {
        this.backgroundTexture = new Texture("Images/map.png");
        this.playerController = playerController;
    }

    public void update() {
        float bgWidth = backgroundTexture.getWidth();
        float bgHeight = backgroundTexture.getHeight();

        int tilesX = (int) Math.ceil(Gdx.graphics.getWidth() / bgWidth) + 2;
        int tilesY = (int) Math.ceil(Gdx.graphics.getHeight() / bgHeight) + 2;

        float cameraX = 0;
        float cameraY = 0;

        float offsetX = (cameraX % bgWidth);
        float offsetY = (cameraY % bgHeight);

        float startX = cameraX - offsetX - bgWidth;
        float startY = cameraY - offsetY - bgHeight;

        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                float tileX = startX + (x * bgWidth);
                float tileY = startY + (y * bgHeight);
                Main.getBatch().draw(backgroundTexture, tileX, tileY);
            }
        }
    }
}
