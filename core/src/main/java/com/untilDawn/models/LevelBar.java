package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LevelBar {
    private Texture backgroundTexture;
    private Texture fillTexture;
    private BitmapFont font;
    private int level;
    private int currentXP;
    private int xpForNextLevel;
    private float progress;
    private float width;
    private float height;
    private GlyphLayout glyphLayout;

    public LevelBar(BitmapFont font, float width, float height) {
        this.font = font;
        this.width = width;
        this.height = height;
        this.level = 1;
        this.currentXP = 0;
        this.xpForNextLevel = calculateXPForNextLevel(level);
        this.progress = 0;
        this.glyphLayout = new GlyphLayout();

        createTextures();
    }

    private void createTextures() {
        Pixmap backgroundPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        backgroundPixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        backgroundPixmap.fill();
        backgroundTexture = new Texture(backgroundPixmap);
        backgroundPixmap.dispose();

        int gradientWidth = 256;
        Pixmap fillPixmap = new Pixmap(gradientWidth, 1, Pixmap.Format.RGBA8888);

        for (int x = 0; x < gradientWidth; x++) {
            float t = (float) x / (gradientWidth - 1);


            int r = 0;
            int g = (int) (30 + t * (150 - 30));
            int b = 1;

            float blueTint = 0.1f * (1 - t);

            fillPixmap.setColor(r / 255f, g / 255f, b / 255f + blueTint, 1f);
            fillPixmap.drawPixel(x, 0);
        }

        fillTexture = new Texture(fillPixmap);
        fillPixmap.dispose();
    }

    public void update(int xp) {
        this.currentXP = xp;

        int tempXP = xp;
        int tempLevel = 1;
        int xpNeeded = 20;

        while (tempXP >= xpNeeded) {
            tempXP -= xpNeeded;
            tempLevel++;
            xpNeeded = calculateXPForNextLevel(tempLevel);
        }

        this.level = tempLevel;
        this.xpForNextLevel = calculateXPForNextLevel(level);
        this.progress = (float) tempXP / xpForNextLevel;
    }

    private int calculateXPForNextLevel(int currentLevel) {
        return 20 * currentLevel;
    }

    public void render(SpriteBatch batch, float screenWidth) {
        float x = 0;
        float y = Gdx.graphics.getHeight() - height;

        batch.draw(backgroundTexture, x, y, screenWidth, height);

        batch.draw(fillTexture, x, y, screenWidth * progress, height);

        String levelText = "Level       " + level;
        glyphLayout.setText(font, levelText);
        float textX = (screenWidth - glyphLayout.width) / 2;
        float textY = y + (height + glyphLayout.height) / 2;

        font.setColor(Color.WHITE);
        font.draw(batch, levelText, textX, textY);
    }

    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (fillTexture != null) {
            fillTexture.dispose();
        }
    }
}
