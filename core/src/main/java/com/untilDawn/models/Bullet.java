package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.models.utils.GameAssetManager;

public class Bullet {
    private Texture texture = new Texture(GameAssetManager.getGameAssetManager().getBullet());
    private Sprite sprite = new Sprite(texture);
    private int damage = 5;
    private int x;
    private int y;
    private Vector2 direction;
    private boolean isActive = true;

    public Bullet(int x, int y) {
        sprite.setSize(20, 20);
        this.x = x;
        this.y = y;
        sprite.setX((float) Gdx.graphics.getWidth() / 2);
        sprite.setY((float) Gdx.graphics.getHeight() / 2);
    }

    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
