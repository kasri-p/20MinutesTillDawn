package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
            System.out.println("AutoAim toggled");
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

    private void spawnRandomEnemy() {
//        EnemyType[] types = {EnemyType.ZOMBIE, EnemyType.SPIDER, EnemyType.GHOST, EnemyType.BAT};
//        EnemyType randomType = types[MathUtils.random(types.length - 1)];

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
            if (bullet == null || !bullet.isActive()) continue;

            Sprite bulletSprite = bullet.getSprite();
            Rectangle bulletRect = bulletSprite.getBoundingRectangle();

            for (Enemy enemy : enemies) {
                if (!enemy.isActive()) continue;

                Rectangle enemyRect = enemy.getBoundingBox();

                if (bulletRect.overlaps(enemyRect)) {
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
                Sprite playerSprite = playerController.getPlayer().getPlayerSprite();
                Rectangle playerRect = playerSprite.getBoundingRectangle();
                Rectangle enemyRect = enemy.getBoundingBox();

                if (enemyRect.overlaps(playerRect)) {
                    // TODO: add damage
                    Gdx.app.log("Collision", "Player collided with enemy: " + enemy.getType().getName());
                }
                if (enemy.isDropActive()) {
                    enemy.collectDrop(playerController.getPlayer());
                }
                continue;
            }

            //TODO: Check if enemy collides with player
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
                int cursorX = (int) (closestEnemy.getPosX() - playerController.getPlayer().getPosX() + Gdx.graphics.getWidth() / 2);
                int cursorY = (int) (Gdx.graphics.getHeight() - (closestEnemy.getPosY() - playerController.getPlayer().getPosY() + Gdx.graphics.getHeight() / 2));

                Gdx.app.log("AutoAim", "Toggled: TRUE");
                Gdx.input.setCursorPosition(cursorX, cursorY);
            }
        }
    }
}
