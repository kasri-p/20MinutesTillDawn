package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
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
    private Rectangle boundingBox;

    // New properties for better collision detection
    private Vector2 position = new Vector2();
    private Vector2 previousPosition = new Vector2();
    private float radius;
    private Circle collisionCircle = new Circle();
    private float speed = 10.0f; // Default speed

    public Bullet(int x, int y) {
        sprite.setSize(20, 20);
        this.x = x;
        this.y = y;
        position.set(x, y);
        previousPosition.set(x, y);
        sprite.setX((float) Gdx.graphics.getWidth() / 2);
        sprite.setY((float) Gdx.graphics.getHeight() / 2);

        // Set up collision
        radius = sprite.getWidth() / 2.5f; // Slightly smaller than the visual size
        collisionCircle.set(x, y, radius);
        boundingBox = new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
    }

    // Update method for bullet physics
    public void update(float delta) {
        if (!isActive) return;

        // Store previous position for trajectory collision
        previousPosition.set(position);

        // Update position based on direction and speed
        if (direction != null) {
            position.x += direction.x * speed * delta * 60; // Normalize by frame rate
            position.y += direction.y * speed * delta * 60;

            // Update sprite and collision shapes
            sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
            collisionCircle.setPosition(position.x, position.y);
            boundingBox.setPosition(position.x - radius, position.y - radius);
        }
    }

    // Getters and setters
    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public float getRadius() {
        return radius;
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
