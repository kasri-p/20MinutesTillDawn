package com.untilDawn.models.utils;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class LightingManager implements Disposable {
    private static LightingManager instance;

    private final Texture lightTexture;
    private final Texture shadowTexture;
    
    private float lightRadius;
    private float ambientIntensity; // 0 = completely dark, 1 = no shadow effect

    private LightingManager() {
        this.lightTexture = createLightTexture();
        this.shadowTexture = createShadowTexture();
        this.lightRadius = 300f;
        this.ambientIntensity = 0.5f; // Increased from 0.4f to 0.5f for more brightness
    }

    public static LightingManager getInstance() {
        if (instance == null) {
            instance = new LightingManager();
        }
        return instance;
    }

    private Texture createLightTexture() {
        Pixmap pixmap = new Pixmap(256, 256, Pixmap.Format.RGBA8888);

        int centerX = 256 / 2;
        int centerY = 256 / 2;
        float radius = 256 / 2f;

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                float distanceFromCenter = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                float normalizedDistance = MathUtils.clamp(distanceFromCenter / radius, 0f, 1f);

                float alpha = 0.3f * (1f - normalizedDistance * normalizedDistance);

                if (normalizedDistance > 0.7f) {
                    alpha *= (1.0f - (normalizedDistance - 0.7f) / 0.3f);
                }

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

        // Brighter shadow overlay - reduced opacity from 0.7f to 0.55f
        batch.setColor(0.18f, 0.17f, 0.22f, 0.55f);

        batch.draw(shadowTexture,
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        float drawSize = lightRadius * 2f;
        // Slightly brighter light
        batch.setColor(0.65f, 0.65f, 0.7f, 0.25f);
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
