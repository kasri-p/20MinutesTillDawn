// core/src/main/java/com/untilDawn/models/utils/LightingManager.java
package com.untilDawn.models.utils;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages lighting effects for the game, creating a bright area around the player
 * while keeping the rest of the map darker.
 */
public class LightingManager implements Disposable {
    private static LightingManager instance;

    private Texture lightTexture;
    private Texture shadowTexture;
    private float lightRadius;
    private float ambientIntensity; // 0 = completely dark, 1 = no shadow effect

    private LightingManager() {
        this.lightTexture = createLightTexture(256);

        this.shadowTexture = createShadowTexture();

        this.lightRadius = 250f;
        this.ambientIntensity = 0.6f;
    }

    public static LightingManager getInstance() {
        if (instance == null) {
            instance = new LightingManager();
        }
        return instance;
    }

    private Texture createLightTexture(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        int centerX = size / 2;
        int centerY = size / 2;
        float radius = size / 2f;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float distanceFromCenter = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                float normalizedDistance = MathUtils.clamp(distanceFromCenter / radius, 0f, 1f);


                float alpha = 0.6f * (1f - normalizedDistance * normalizedDistance);

                pixmap.setColor(1f, 1f, 1f, alpha);
                pixmap.drawPixel(x, y);
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createShadowTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }


    public void render(SpriteBatch batch, OrthographicCamera camera, float playerX, float playerY) {
        Color oldColor = batch.getColor().cpy();
        float oldPackedColor = batch.getPackedColor();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setColor(0, 0, 0, 1f - ambientIntensity);
        batch.draw(shadowTexture,
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        float drawSize = lightRadius * 2f;
        batch.setColor(0.7f, 0.7f, 0.7f, 0.5f);
        batch.draw(lightTexture,
            playerX - lightRadius,
            playerY - lightRadius,
            drawSize,
            drawSize);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setColor(oldColor);
    }

    public float getLightRadius() {
        return lightRadius;
    }

    public void setLightRadius(float radius) {
        this.lightRadius = radius;
    }

    public float getAmbientIntensity() {
        return ambientIntensity;
    }

    public void setAmbientIntensity(float intensity) {
        // Clamp between 0 and 1
        this.ambientIntensity = MathUtils.clamp(intensity, 0f, 1f);
    }

    @Override
    public void dispose() {
        if (lightTexture != null) {
            lightTexture.dispose();
        }
        if (shadowTexture != null) {
            shadowTexture.dispose();
        }
    }
}
