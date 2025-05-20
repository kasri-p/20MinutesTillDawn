package com.untilDawn.controllers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.Main;
import com.untilDawn.models.Bullet;
import com.untilDawn.models.Enemy;
import com.untilDawn.models.enums.EnemyType;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.Iterator;

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
    private int numberOfTrees = 20;

    public EnemyController(PlayerController playerController, WeaponController weaponController, float mapWidth, float mapHeight) {
        this.playerController = playerController;
        this.weaponController = weaponController;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.currentSpawnRate = initialSpawnRate;
    }

    public void update(float delta) {
        gameTime += delta;

        // Place trees if not done yet
        if (!treesPlaced) {
            placeTrees();
        }

        // Update spawn rate based on elapsed time
        updateSpawnRate();

        // Check if it's time to spawn a new enemy
        spawnTimer += delta;
        if (spawnTimer >= currentSpawnRate) {
            spawnRandomEnemy();
            spawnTimer = 0;
        }

        // Update all existing enemies
        updateEnemies(delta);

        // Check for collisions with bullets
        checkBulletCollisions();

        // Check for collisions with player
        checkPlayerCollisions();

        // Draw all active enemies
        drawEnemies();
    }

    private void placeTrees() {
        for (int i = 0; i < numberOfTrees; i++) {
            float x = MathUtils.random(100, mapWidth - 100);
            float y = MathUtils.random(100, mapHeight - 100);

            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();
            float distanceToPlayer = Vector2.dst(x, y, playerX, playerY);

            if (distanceToPlayer > 300) {
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

    private void spawnRandomEnemy() {
//        EnemyType[] types = {EnemyType.ZOMBIE, EnemyType.SPIDER, EnemyType.GHOST, EnemyType.BAT};
//        EnemyType randomType = types[MathUtils.random(types.length - 1)];

        // Get a random spawn position at the edge of the map
        Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);

// TODO       Enemy newEnemy = new Enemy(randomType, spawnPos.x, spawnPos.y);
//        enemies.add(newEnemy);
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

    private void checkBulletCollisions() {
        ArrayList<Bullet> bullets = weaponController.getBullets();
        if (bullets == null) return;

        for (Bullet bullet : bullets) {
            if (bullet == null) continue;

            float bulletX = bullet.getSprite().getX() + bullet.getSprite().getWidth() / 2;
            float bulletY = bullet.getSprite().getY() + bullet.getSprite().getHeight() / 2;

            for (Enemy enemy : enemies) {
                if (!enemy.isActive()) continue;

                float enemyX = enemy.getPosX();
                float enemyY = enemy.getPosY();
                float distance = Vector2.dst(bulletX, bulletY, enemyX, enemyY);

                if (distance < 30) {
                    boolean killed = enemy.hit(bullet.getDamage());

                    bullet.setActive(false);

                    break;
                }
            }
        }
    }

    private void checkPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) {
                // Check for drop collection if enemy is not active
                if (enemy.isDropActive()) {
                    enemy.collectDrop(playerController.getPlayer());
                }
                continue;
            }

            //TODO: Check if enemy collides with player
        }
    }

    private void drawEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                Animation animation = GameAssetManager.getGameAssetManager().getEnemyAnimation(enemy.getType().getName());
                animation.setPlayMode(Animation.PlayMode.LOOP);
                animation.getKeyFrame(gameTime, true);
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
}
