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

    // For dropped items
    private boolean hasDroppedItem = false;
    private Texture dropTexture;
    private Sprite dropSprite;
    private boolean dropActive = false;

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
        int side = MathUtils.random(3);

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
            default: // Fallback
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

        // Only move if this enemy type can move
        if (type.canMove()) {
            moveTowardsPlayer(player, delta);
        }

        // Update sprite position
        sprite.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);

        // Update bounding box
        boundingBox.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);

        // Handle drop item update if it exists
        if (dropActive && dropSprite != null) {
            // Pulsate the drop or add effects here if needed
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
    }

    public boolean hit(int damage) {
        health -= damage;

        // Check if enemy is defeated
        if (health <= 0 && isActive) {
            isActive = false;
            dropItem();
            return true;
        }

        return false;
    }

    private void dropItem() {
        if (!hasDroppedItem) {
            hasDroppedItem = true;
            dropActive = true;

            // Load appropriate drop texture based on enemy type
            String dropTexturePath = "Images/drops/" + ".png";
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
    }

    public boolean collectDrop(Player player) {
        if (!dropActive) return false;

        // Create a temporary rectangle for the drop
        Rectangle dropRect = new Rectangle(
            dropSprite.getX(),
            dropSprite.getY(),
            dropSprite.getWidth(),
            dropSprite.getHeight()
        );

        // Check if player collides with the drop
        if (dropRect.overlaps(player.getBoundingBox())) {
            applyDropEffect(player);
            dropActive = false;
            return true;
        }

        return false;
    }

    private void applyDropEffect(Player player) {
//        switch (type.getDropType()) {
//            case "health":
//                // Increase player health
//                player.setPlayerHealth(Math.min(player.getPlayerHealth() + 10, 100));
//                break;
//            case "speed":
//                // Temporary speed boost - would need a timer system
//                break;
//            case "ammo":
//                // Would increase weapon ammo
//                break;
//            case "experience":
//                // Would increase player XP or score
//                break;
//            case "seed":
//                // Special drop for trees
//                break;
//        }
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
