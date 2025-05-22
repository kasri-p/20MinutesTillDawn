package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.models.enums.EnemyType;
import com.untilDawn.models.utils.GameAssetManager;

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
    private String dropType;

    // Improved properties for hit flash effect
    private boolean isFlashing = false;
    private float flashDuration = 0.4f;  // Longer duration for smoother effect
    private float flashTimer = 0;
    private Color originalColor = new Color(1f, 1f, 1f, 1f);
    private Color flashColor = new Color(1f, 0.3f, 0.3f, 1f);  // Less intense red

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
            case 2:
                x = MathUtils.random(marginFromEdge, mapWidth - marginFromEdge);
                y = marginFromEdge;
                break;
            case 3:
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
        sprite.setColor(originalColor);

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

        // Update hit flash effect
        updateFlashEffect(delta);

        if (dropActive && dropSprite != null) {
            float pulsate = 0.7f + 0.3f * (float) Math.sin(spawnTime * 3);
            dropSprite.setAlpha(pulsate);

            dropSprite.setRotation(dropSprite.getRotation() + 60 * delta);
        }
    }

    private void updateFlashEffect(float delta) {
        if (isFlashing) {
            flashTimer += delta;
            if (flashTimer >= flashDuration) {
                // Flash ended, return to normal color
                isFlashing = false;
                flashTimer = 0;
                sprite.setColor(originalColor);
            } else {
                // Create a smoother transition from flash color to original color
                float progress = flashTimer / flashDuration;

                // Use a smoother easing function
                float smoothProgress = 1 - (1 - progress) * (1 - progress);

                // Interpolate between flash color and original color
                Color currentColor = new Color(
                    flashColor.r + (originalColor.r - flashColor.r) * smoothProgress,
                    flashColor.g + (originalColor.g - flashColor.g) * smoothProgress,
                    flashColor.b + (originalColor.b - flashColor.b) * smoothProgress,
                    1f
                );

                sprite.setColor(currentColor);
            }
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

        startFlashEffect();

        if (health <= 0 && isActive) {
            isActive = false;
            dropItem();
            if (type == EnemyType.EYEBAT) {
                GameAssetManager.getGameAssetManager().playBatDeath();
            } else if (type != EnemyType.TREE) {
                GameAssetManager.getGameAssetManager().playSplash();
            }
            return true;
        }

        return false;
    }

    private void startFlashEffect() {
        isFlashing = true;
        flashTimer = 0;

        Color initialFlash = new Color(
            originalColor.r * 0.4f + flashColor.r * 0.6f,
            originalColor.g * 0.4f + flashColor.g * 0.6f,
            originalColor.b * 0.4f + flashColor.b * 0.6f,
            1f
        );
        sprite.setColor(initialFlash);
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
        String dropTexturePath = "Images/drops/" + dropType + ".png";
        int size = dropType.equals("experience") ? 12 : 30;
        try {
            dropTexture = new Texture(Gdx.files.internal(dropTexturePath));
            dropSprite = new Sprite(dropTexture);
            dropSprite.setSize(size, size);
            dropSprite.setPosition(posX - 15, posY - 15);
            dropSprite.setOriginCenter();
        } catch (Exception e) {
            System.out.println("Error loading drop texture: " + e.getMessage());
            dropActive = false;
        }
    }

    public boolean collectDrop(Player player) {
        if (!dropActive) return false;

        Rectangle dropRect = new Rectangle(
            dropSprite.getX(),
            dropSprite.getY(),
            dropSprite.getWidth(),
            dropSprite.getHeight()
        );

        if (dropRect.overlaps(player.getBoundingBox())) {
            applyDropEffect(player);
            dropActive = false;
            GameAssetManager.getGameAssetManager().playObtain();
            return true;
        }
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
                break;

            case "experience":
                player.addXP(3);
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

    public Sprite getSprite() {
        return sprite;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (dropTexture != null) dropTexture.dispose();
    }

    public void setFlashDuration(float duration) {
        this.flashDuration = duration;
    }
}
