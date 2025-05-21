package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.models.enums.EnemyType;

public class Enemy {
    private final EnemyType type;
    private int health;
    private float posX;
    private float posY;
    private Texture texture;
    private Sprite sprite;
    private Rectangle boundingBox;
    private boolean isActive = true;
    private Vector2 direction = new Vector2(0, 0);
    private float spawnTime;

    // For dropped items // TODO: add these to enemyController
    private boolean hasDroppedItem = false;
    private Texture dropTexture;
    private Sprite dropSprite;
    private boolean dropActive = false;
    private String dropType;

    public Enemy(EnemyType type, float posX, float posY) {
        this.type = type;
        this.health = type.getHealth();
        this.posX = posX;
        this.posY = posY;
        this.spawnTime = 0;

        loadTexture();
        createSprite();
    }

    public static Vector2 getRandomSpawnPosition(float mapWidth, float mapHeight, float marginFromEdge) {
        float x, y;
        int side = MathUtils.random(3); // 0 = top, 1 = right, 2 = bottom, 3 = left

        switch (side) {
            case 0: // Top
                x = MathUtils.random(marginFromEdge, mapWidth - marginFromEdge);
                y = mapHeight - marginFromEdge;
                break;
            case 1: // Right
                x = mapWidth - marginFromEdge;
                y = MathUtils.random(marginFromEdge, mapHeight - marginFromEdge);
                break;
            case 2: // Bottom
                x = MathUtils.random(marginFromEdge, mapWidth - marginFromEdge);
                y = marginFromEdge;
                break;
            case 3: // Left
                x = marginFromEdge;
                y = MathUtils.random(marginFromEdge, mapHeight - marginFromEdge);
                break;
            default: // Fallback (shouldn't happen)
                x = MathUtils.random(mapWidth);
                y = MathUtils.random(mapHeight);
        }

        return new Vector2(x, y);
    }

    private void loadTexture() {
        try {
            this.texture = new Texture(Gdx.files.internal(type.getTexturePath()));
        } catch (Exception e) {
            this.texture = new Texture(Gdx.files.internal("Images/enemies/default.png"));
            System.out.println("Error loading enemy texture: " + e.getMessage());
        }
    }

    private void createSprite() {
        this.sprite = new Sprite(texture);

        float scale = 1.0f;

        // Make trees significantly larger
        if (type == EnemyType.TREE) {
            scale = 2.3f; // Increase tree size by 2.5x
        }

        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setOriginCenter();
        sprite.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);

        this.boundingBox = new Rectangle(
            posX - sprite.getWidth() / 2,
            posY - sprite.getHeight() / 2,
            sprite.getWidth(),
            sprite.getHeight()
        );
    }

    public void update(float delta, Player player) {
        // Only update if active
        if (!isActive) return;

        spawnTime += delta;

        if (type.canMove()) {
            moveTowardsPlayer(player, delta);
        }

        sprite.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);

        boundingBox.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);

        if (dropActive && dropSprite != null) {
            float pulsate = 0.7f + 0.3f * (float) Math.sin(spawnTime * 3);
            dropSprite.setAlpha(pulsate);

            dropSprite.setRotation(dropSprite.getRotation() + 60 * delta);
        }
    }

    private void moveTowardsPlayer(Player player, float delta) {
        float playerX = player.getPosX();
        float playerY = player.getPosY();

        direction.x = playerX - posX;
        direction.y = playerY - posY;
        direction.nor();

        posX += direction.x * type.getSpeed() * delta * 60;
        posY += direction.y * type.getSpeed() * delta * 60;

        if (direction.x < 0) {
            sprite.setFlip(true, false);
        } else if (direction.x > 0) {
            sprite.setFlip(false, false);
        }
    }

    public boolean hit(int damage) {
        health -= damage;

        if (health <= 0 && isActive) {
            isActive = false;
            dropItem();
            return true;
        }

        return false;
    }

    private void dropItem() {
        if (hasDroppedItem) return;

        hasDroppedItem = true;

        float dropChance;

        if (type == EnemyType.TREE) {
            dropChance = 0.8f;
            dropType = "health";
        } else {
            dropChance = 1.0f;
            dropType = "experience";
        }

        if (MathUtils.random() < dropChance) {
            createDropSprite();
            dropActive = true;
        }
    }

    private void createDropSprite() {
        // TODO
        String dropTexturePath = "Images/drops/" + dropType + ".png";

        try {
            dropTexture = new Texture(Gdx.files.internal(dropTexturePath));
            dropSprite = new Sprite(dropTexture);
            dropSprite.setSize(30, 30);
            dropSprite.setPosition(posX - 15, posY - 15);
            dropSprite.setOriginCenter();
        } catch (Exception e) {
            System.out.println("Error loading drop texture: " + e.getMessage());
            dropActive = false;
        }
    }

    public boolean collectDrop(Player player) {
//        if (!dropActive) return false;
//
//        Rectangle dropRect = new Rectangle(
//            dropSprite.getX(),
//            dropSprite.getY(),
//            dropSprite.getWidth(),
//            dropSprite.getHeight()
//        );
//
//        if (dropRect.overlaps(player.getBoundingBox())) {
//            applyDropEffect(player);
//            dropActive = false;
//            return true;
//        }
//
        return false;
    }

    private void applyDropEffect(Player player) {
        switch (dropType) {
            case "health":
                int health = player.getPlayerHealth();
                health += 1;
                if (player.getCharacter().getHp() < health) {
                    health = player.getCharacter().getHp();
                }
                player.setPlayerHealth(health);
                Gdx.app.log("Drop", "Player collected health drop. New health: " + player.getPlayerHealth());
                break;

            case "experience":
                player.addXP(3);
                Gdx.app.log("Drop", "Player collected experience drop. New score: " + App.getLoggedInUser().getScore());
                break;

            default:
                Gdx.app.log("Drop", "Player collected unknown drop type: " + dropType);
                break;
        }
    }

    public Sprite getDropSprite() {
        return dropSprite;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDropActive() {
        return dropActive;
    }

    public EnemyType getType() {
        return type;
    }

    public int getHealth() {
        return health;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (dropTexture != null) dropTexture.dispose();
    }
}
