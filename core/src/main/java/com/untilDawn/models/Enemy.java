package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.models.enums.EnemyType;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;

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

    private boolean isFlashing = false;
    private float flashDuration = 0.4f;
    private float flashTimer = 0;
    private Color originalColor = new Color(1f, 1f, 1f, 1f);
    private Color flashColor = new Color(1f, 0.3f, 0.3f, 1f);

    private float shootTimer = 0f;
    private ArrayList<EnemyBullet> bullets = new ArrayList<>();

    private boolean isKnockedBack = false;
    private float knockbackTimer = 0f;
    private float knockbackDuration = 0.5f;
    private Vector2 knockbackDirection = new Vector2();
    private float knockbackForce = 100f;

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
            default:
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

        if (type == EnemyType.TREE) {
            scale = 2.3f;
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
        if (!isActive) return;

        spawnTime += delta;

        updateKnockback(delta);

        if (!isKnockedBack && type.canMove()) {
            moveTowardsPlayer(player, delta);
        }

        if (type.canShoot() && type == EnemyType.EYEBAT) {
            updateShooting(delta, player);
        }

        updateBullets(delta);

        sprite.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);
        boundingBox.setPosition(posX - sprite.getWidth() / 2, posY - sprite.getHeight() / 2);

        updateFlashEffect(delta);

        if (dropActive && dropSprite != null) {
            float pulsate = 0.7f + 0.3f * (float) Math.sin(spawnTime * 3);
            dropSprite.setAlpha(pulsate);
            dropSprite.setRotation(dropSprite.getRotation() + 60 * delta);
        }
    }

    private void updateKnockback(float delta) {
        if (isKnockedBack) {
            knockbackTimer += delta;

            // Apply knockback movement
            float knockbackSpeed = knockbackForce * (1 - knockbackTimer / knockbackDuration);
            posX += knockbackDirection.x * knockbackSpeed * delta;
            posY += knockbackDirection.y * knockbackSpeed * delta;

            if (knockbackTimer >= knockbackDuration) {
                isKnockedBack = false;
                knockbackTimer = 0f;
            }
        }
    }

    private void updateShooting(float delta, Player player) {
        shootTimer += delta;

        float shootInterval = 6f;
        if (shootTimer >= shootInterval) {
            shootAtPlayer(player);
            shootTimer = 0f;
        }
    }

    private void shootAtPlayer(Player player) {
        if (player == null) return;

        float dirX = player.getPosX() - posX;
        float dirY = player.getPosY() - posY;
        Vector2 shootDirection = new Vector2(dirX, dirY).nor();

        EnemyBullet bullet = new EnemyBullet(posX, posY, shootDirection);
        bullets.add(bullet);
    }

    private void updateBullets(float delta) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            EnemyBullet bullet = bullets.get(i);
            bullet.update(delta);

            if (!bullet.isActive()) {
                bullet.dispose();
                bullets.remove(i);
            }
        }
    }

    private void updateFlashEffect(float delta) {
        if (isFlashing) {
            flashTimer += delta;
            if (flashTimer >= flashDuration) {
                isFlashing = false;
                flashTimer = 0;
                sprite.setColor(originalColor);
            } else {
                float progress = flashTimer / flashDuration;

                float smoothProgress = 1 - (1 - progress) * (1 - progress);

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

        // Apply knockback when hit (except for trees)
        if (type != EnemyType.TREE) {
            applyKnockback();
        }

        if (health <= 0 && isActive) {
            isActive = false;
            dropItem();
            if (type == EnemyType.EYEBAT) {
                GameAssetManager.getGameAssetManager().playBatDeath();
            } else if (type != EnemyType.TREE) {
                GameAssetManager.getGameAssetManager().playSplash();
            }
            App.getGame().getPlayer().addKill();
            return true;
        }

        return false;
    }

    private void applyKnockback() {
        isKnockedBack = true;
        knockbackTimer = 0f;

        if (direction.len() > 0) {
            knockbackDirection.set(-direction.x, -direction.y).nor();
        } else {
            float angle = MathUtils.random(0, 2 * MathUtils.PI);
            knockbackDirection.set(MathUtils.cos(angle), MathUtils.sin(angle));
        }
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

    public void collectDrop(Player player) {
        if (!dropActive) return;

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
        }
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

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public ArrayList<EnemyBullet> getBullets() {
        return bullets;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
        if (dropTexture != null) dropTexture.dispose();

        for (EnemyBullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();
    }

    public void setFlashDuration(float duration) {
        this.flashDuration = duration;
    }

    public static class EnemyBullet {
        private final Sprite sprite;
        private Texture texture;
        private Vector2 position = new Vector2();
        private Vector2 direction = new Vector2();
        private boolean isActive = true;
        private Rectangle boundingBox;
        private Circle collisionCircle = new Circle();
        private float speed = 8.0f;
        private float radius;
        private float lifeTime = 0f;
        private float maxLifeTime = 5f;

        public EnemyBullet(float x, float y, Vector2 direction) {
            createTexture();

            this.sprite = new Sprite(texture);
            sprite.setSize(16, 16);
            this.position.set(x, y);
            this.direction.set(direction).nor();

            sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

            radius = sprite.getWidth() / 2.0f;
            collisionCircle.set(x, y, radius);
            boundingBox = new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
        }

        private void createTexture() {
            try {
                texture = new Texture(Gdx.files.internal("Images/Enemies/eyebat/projectile.png"));
            } catch (Exception e) {
                texture = new Texture(Gdx.files.internal("Images/bullet.png"));
            }
        }

        public void update(float delta) {
            if (!isActive) return;

            lifeTime += delta;

            if (lifeTime >= maxLifeTime) {
                isActive = false;
                return;
            }

            position.x += direction.x * speed * delta * 60;
            position.y += direction.y * speed * delta * 60;

            sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
            sprite.setColor(Color.RED);
            collisionCircle.setPosition(position.x, position.y);
            boundingBox.setPosition(position.x - radius, position.y - radius);
        }

        public Texture getTexture() {
            return texture;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public Vector2 getPosition() {
            return position;
        }

        public Vector2 getDirection() {
            return direction;
        }

        public float getSpeed() {
            return speed;
        }

        public Circle getCollisionCircle() {
            return collisionCircle;
        }

        public float getRadius() {
            return radius;
        }

        public Rectangle getBoundingBox() {
            return boundingBox;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            this.isActive = active;
        }

        public void dispose() {
            if (texture != null) {
                texture.dispose();
            }
        }


    }
}
