package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Bullet;
import com.untilDawn.models.Enemy;
import com.untilDawn.models.enums.EnemyType;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class EnemyController {
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private PlayerController playerController;
    private WeaponController weaponController;

    private float spawnTimer = 0;
    private float initialSpawnRate = 3.0f; // One enemy every 3 seconds initially
    private float currentSpawnRate; // This will decrease over time
    private float minimumSpawnRate = 0.5f; // Max of 2 enemies per second
    private float spawnRateDecreasePerMinute = 0.2f; // Spawn rate decreases by 0.2 seconds every minute

    private float gameTime = 0;
    private float mapWidth;
    private float mapHeight;

    private boolean treesPlaced = false;
    private int numberOfTrees = 30;

    private boolean autoAim = false;
    private float autoAimCooldown = 0;

    // Helper variables for collision detection
    private Circle bulletCircle = new Circle();
    private Circle enemyCircle = new Circle();
    private Vector2 bulletVelocity = new Vector2();
    private Vector2 prevBulletPos = new Vector2();
    private Vector2 currentBulletPos = new Vector2();

    private int tentacleSpawned = 0;
    private int eyeBatSpawned = 0;

    public EnemyController(PlayerController playerController, WeaponController weaponController, float mapWidth, float mapHeight) {
        this.playerController = playerController;
        this.weaponController = weaponController;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.currentSpawnRate = initialSpawnRate;
    }

    public void update(float delta) {
        Map<String, String> keyBindings = App.getKeybinds();
        if (autoAimCooldown <= 0 && Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBindings.get("Auto Shoot")))) {
            autoAim = !autoAim;
            autoAimCooldown = 0.2f;
        } else {
            autoAimCooldown -= delta;
        }

        gameTime += delta;

        if (!treesPlaced) {
            placeTrees();
        }

        updateSpawnRate();

        while (gameTime >= 30 + 3 * tentacleSpawned) {
            spawnTentacleMonster();
            tentacleSpawned++;
        }

        float eyebatStartTime = 30 + 4 * eyeBatSpawned - gameTime;
        if (gameTime >= eyebatStartTime) {
            while (gameTime >= 30 + 10 * eyeBatSpawned) {
                spawnEyeBat();
                eyeBatSpawned++;
            }
        }

        // Update all existing enemies
        updateEnemies(delta);

        // Check for collisions with bullets
        checkBulletCollisions(delta);

        // Check for collisions with player
        checkPlayerCollisions();

        // Draw all active enemies
        drawEnemies();

        // check auto aim
        checkAutoAim();
    }

    private void placeTrees() {
        for (int i = 0; i < numberOfTrees; i++) {
            float x = MathUtils.random(100, mapWidth - 100);
            float y = MathUtils.random(100, mapHeight - 100);

            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();
            float distanceToPlayer = Vector2.dst(x, y, playerX, playerY);

            if (distanceToPlayer > 200) {
                Enemy tree = new Enemy(EnemyType.TREE, x, y);
                enemies.add(tree);
            } else {
                i--;
            }
        }

        treesPlaced = true;
    }

    private void updateSpawnRate() {
        float minutesElapsed = gameTime / 60f;
        currentSpawnRate = initialSpawnRate - (minutesElapsed * spawnRateDecreasePerMinute);

        if (currentSpawnRate < minimumSpawnRate) {
            currentSpawnRate = minimumSpawnRate;
        }
    }

    private void spawnTentacleMonster() {
        System.out.println("spawned one tentacle monster");
    }

    private void spawnEyeBat() {
        System.out.println("spawned one eye bat");
    }

    private void updateEnemies(float delta) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            enemy.update(delta, playerController.getPlayer());

            if (!enemy.isActive() && !enemy.isDropActive()) {
                enemy.dispose();
                iterator.remove();
            }
        }
    }

    private void checkBulletCollisions(float delta) {
        ArrayList<Bullet> bullets = weaponController.getBullets();
        if (bullets == null) return;

        for (Bullet bullet : bullets) {
            if (bullet == null || !bullet.isActive()) continue;

            Sprite bulletSprite = bullet.getSprite();

            // Get bullet position and size for better collision detection
            float bulletX = bulletSprite.getX() + bulletSprite.getWidth() / 2;
            float bulletY = bulletSprite.getY() + bulletSprite.getHeight() / 2;
            float bulletRadius = Math.min(bulletSprite.getWidth(), bulletSprite.getHeight()) / 2.5f;

            // Store previous position for line-based collision
            prevBulletPos.set(currentBulletPos);
            currentBulletPos.set(bulletX, bulletY);

            // For the first frame, initialize both positions
            if (prevBulletPos.x == 0 && prevBulletPos.y == 0) {
                prevBulletPos.set(currentBulletPos);
            }

            // Calculate bullet velocity for trajectory checking
            bulletVelocity.set(bullet.getDirection()).scl(10f); // Adjust speed multiplier as needed

            // Setup circle for bullet
            bulletCircle.set(bulletX, bulletY, bulletRadius);

            for (Enemy enemy : enemies) {
                if (!enemy.isActive()) continue;

                // Get enemy position and size for better collision detection
                float enemyX = enemy.getPosX();
                float enemyY = enemy.getPosY();
                Rectangle boundingBox = enemy.getBoundingBox();
                float enemyRadius = Math.max(boundingBox.width, boundingBox.height) / 2.5f;

                // Setup circle for enemy
                enemyCircle.set(enemyX, enemyY, enemyRadius);

                boolean collision = false;

                if (Intersector.overlaps(bulletCircle, enemyCircle)) {
                    collision = true;
                } else if (Intersector.intersectSegmentCircle(prevBulletPos, currentBulletPos,
                    new Vector2(enemyX, enemyY), enemyRadius * enemyRadius)) {
                    collision = true;
                }

                if (collision) {
                    boolean killed = enemy.hit(bullet.getDamage());
                    bullet.setActive(false);

                    // createHitEffect(bulletX, bulletY);

                    break;
                }
            }
        }
    }

    private void checkPlayerCollisions() {
        if (playerController == null || playerController.getPlayer() == null) return;

        Sprite playerSprite = playerController.getPlayer().getPlayerSprite();
        Rectangle playerRect = playerSprite.getBoundingRectangle();

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();
        float playerRadius = Math.min(playerRect.width, playerRect.height) / 2.5f;

        Circle playerCircle = new Circle(playerX, playerY, playerRadius);

        for (Enemy enemy : enemies) {
            Rectangle enemyRect = enemy.getBoundingBox();
            float enemyX = enemy.getPosX();
            float enemyY = enemy.getPosY();
            float enemyRadius = Math.max(enemyRect.width, enemyRect.height) / 2.5f;

            Circle enemyCircle = new Circle(enemyX, enemyY, enemyRadius);

            if (enemy.isActive() && Intersector.overlaps(playerCircle, enemyCircle)) {
                // TODO: Add damage to player
                Gdx.app.log("Collision", "Player collided with enemy: " + enemy.getType().getName());
            } else if (!enemy.isActive() && enemy.isDropActive()) {
                if (Intersector.overlaps(playerCircle, enemyCircle)) {
                    enemy.collectDrop(playerController.getPlayer());
                }
            }
        }
    }

    private void drawEnemies() {
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();

        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                Animation<Texture> animation = assetManager.getEnemyAnimation(enemy.getType().getName());
                if (animation != null) {
                    animation.setPlayMode(Animation.PlayMode.LOOP);
                    Texture currentFrame = animation.getKeyFrame(gameTime, true);

                    Sprite sprite = new Sprite(currentFrame);
                    sprite.setPosition(enemy.getPosX() - sprite.getWidth() / 2, enemy.getPosY() - sprite.getHeight() / 2);
                    sprite.draw(Main.getBatch());
                }
            } else if (enemy.isDropActive() && enemy.getDropSprite() != null) {
                enemy.getDropSprite().draw(Main.getBatch());
            }
        }
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    private void checkAutoAim() {
        if (autoAim && !enemies.isEmpty()) {
            Enemy closestEnemy = null;
            float minDistance = Float.MAX_VALUE;

            for (Enemy enemy : enemies) {
                if (!enemy.isActive()) continue;

                float distance = Vector2.dst(
                    playerController.getPlayer().getPosX(),
                    playerController.getPlayer().getPosY(),
                    enemy.getPosX(),
                    enemy.getPosY()
                );

                if (distance < minDistance) {
                    minDistance = distance;
                    closestEnemy = enemy;
                }
            }

            if (closestEnemy != null) {
                int cursorX = (int) (closestEnemy.getPosX() - playerController.getPlayer().getPosX() + (float) Gdx.graphics.getWidth() / 2);
                int cursorY = (int) (Gdx.graphics.getHeight() - (closestEnemy.getPosY() - playerController.getPlayer().getPosY() + (float) Gdx.graphics.getHeight() / 2));

                Gdx.input.setCursorPosition(cursorX, cursorY);
            }
        }
    }
}
