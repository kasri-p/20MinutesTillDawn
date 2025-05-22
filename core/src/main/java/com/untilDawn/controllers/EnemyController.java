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
    private float initialSpawnRate = 3.0f; // One enemy every 3 seconds initially
    private float currentSpawnRate; // This will decrease over time
    private float minimumSpawnRate = 0.5f; // Max of 2 enemies per second
    private float spawnRateDecreasePerMinute = 0.2f; // Spawn rate decreases by 0.2 seconds every minute
    private float gameTime = 0;
    private float timeSinceLastSpawn = 0;
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
        timeSinceLastSpawn += delta;

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
        ArrayList<Circle> treePositions = new ArrayList<>();

        for (int i = 0; i < numberOfTrees; i++) {
            float x = MathUtils.random(100, mapWidth - 100);
            float y = MathUtils.random(100, mapHeight - 100);

            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            // Check distance from player
            float distanceToPlayer = Vector2.dst(x, y, playerX, playerY);

            // Define minimum distance between trees
            float treeRadius = 75f; // Adjust based on your tree size
            boolean tooCloseToOtherTree = false;

            // Check distance from other trees
            for (Circle existingTree : treePositions) {
                float distanceToTree = Vector2.dst(x, y, existingTree.x, existingTree.y);
                if (distanceToTree < (treeRadius * 2)) {
                    tooCloseToOtherTree = true;
                    break;
                }
            }

            if (distanceToPlayer > 300 && !tooCloseToOtherTree) {
                Enemy tree = new Enemy(EnemyType.TREE, x, y);
                enemies.add(tree);

                treePositions.add(new Circle(x, y, treeRadius));
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

    private void spawnRegularEnemy() {
        Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);

        Enemy enemy = new Enemy(EnemyType.TENTACLE, spawnPos.x, spawnPos.y);
        enemies.add(enemy);

        Gdx.app.log("EnemyController", "Spawned regular enemy at " + spawnPos.x + ", " + spawnPos.y);
    }

    private void spawnTentacleMonster() {
        Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);

        Enemy enemy = new Enemy(EnemyType.TENTACLE, spawnPos.x, spawnPos.y);
        enemies.add(enemy);

        Gdx.app.log("EnemyController", "Spawned tentacle monster at " + spawnPos.x + ", " + spawnPos.y);
    }

    private void spawnEyeBat() {
        Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);

        Enemy enemy = new Enemy(EnemyType.EYEBAT, spawnPos.x, spawnPos.y);
        enemies.add(enemy);

        Gdx.app.log("EnemyController", "Spawned eye bat at " + spawnPos.x + ", " + spawnPos.y);
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

            float bulletX = bulletSprite.getX() + bulletSprite.getWidth() / 2;
            float bulletY = bulletSprite.getY() + bulletSprite.getHeight() / 2;

            float bulletRadius = Math.min(bulletSprite.getWidth(), bulletSprite.getHeight()) / 3.0f;

            prevBulletPos.set(currentBulletPos);
            currentBulletPos.set(bulletX, bulletY);

            if (prevBulletPos.x == 0 && prevBulletPos.y == 0) {
                prevBulletPos.set(currentBulletPos);
            }

            bulletVelocity.set(bullet.getDirection()).scl(15f);

            bulletCircle.set(bulletX, bulletY, bulletRadius);

            for (Enemy enemy : enemies) {
                if (!enemy.isActive()) continue;

                float enemyX = enemy.getPosX();
                float enemyY = enemy.getPosY();
                Rectangle boundingBox = enemy.getBoundingBox();

                float enemyRadius;

                if (enemy.getType() == EnemyType.TREE) {
                    enemyRadius = Math.max(boundingBox.width, boundingBox.height) / 3.0f;
                } else {
                    enemyRadius = Math.max(boundingBox.width, boundingBox.height) / 2.5f;
                }

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

                    if (killed) {
                        dropItem(enemy);
                    }

                    break;
                }
            }
        }
    }

    private void dropItem(Enemy enemy) {
        if (enemy.getType() == EnemyType.TREE) {
            // TODO
        }

        float roll = MathUtils.random(0f, 100f);
        String dropType = null;
//
//        if (roll < HEALTH_DROP_CHANCE) {
//            dropType = "health";
//        } else if (roll < HEALTH_DROP_CHANCE + SPEED_DROP_CHANCE) {
//            dropType = "speed";
//        } else if (roll < HEALTH_DROP_CHANCE + SPEED_DROP_CHANCE + AMMO_DROP_CHANCE) {
//            dropType = "ammo";
//        }

//        // If we decided to drop something, create the drop
//        if (dropType != null) {
//            createDrop(enemy, dropType);
//        }
    }


//    private void createDrop(Enemy enemy, String dropType) {
//        // The actual implementation will be handled by the Enemy class
//        // Just logging here to show the concept
//        Gdx.app.log("EnemyController", "Created " + dropType + " drop at " +
//            enemy.getPosX() + ", " + enemy.getPosY());
//    }

    private void checkPlayerCollisions() {
        if (playerController == null || playerController.getPlayer() == null) return;

        Sprite playerSprite = playerController.getPlayer().getPlayerSprite();
        Rectangle playerRect = playerSprite.getBoundingRectangle();

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float playerRadius = Math.min(playerRect.width, playerRect.height) / 3.0f;

        Circle playerCircle = new Circle(playerX, playerY, playerRadius);

        for (Enemy enemy : enemies) {
            Rectangle enemyRect = enemy.getBoundingBox();
            float enemyX = enemy.getPosX();
            float enemyY = enemy.getPosY();

            float enemyRadius;

            if (enemy.getType() == EnemyType.TREE) {
                enemyRadius = Math.max(enemyRect.width, enemyRect.height) / 3.0f;
            } else {
                enemyRadius = Math.max(enemyRect.width, enemyRect.height) / 2.5f;
            }

            Circle enemyCircle = new Circle(enemyX, enemyY, enemyRadius);

            if (enemy.isActive() && Intersector.overlaps(playerCircle, enemyCircle)) {
                Gdx.app.log("Player Health", "Player health: " + playerController.getPlayer().getPlayerHealth());

                playerController.getPlayer().setPlayerHealth(
                    playerController.getPlayer().getPlayerHealth() - 1
                );

                Gdx.app.log("Collision", "Player collided with enemy: " + enemy.getType().getName());
                return;
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

                    Sprite sprite = enemy.getSprite();
                    sprite.setTexture(currentFrame);

                    float scale = 1.0f;

                    if (enemy.getType() == EnemyType.TREE) {
                        scale = 2.2f;
                    } else if (enemy.getType() == EnemyType.TENTACLE) {
                        scale = 1.5f;
                    }

                    sprite.setSize(currentFrame.getWidth() * scale, currentFrame.getHeight() * scale);
                    sprite.setPosition(enemy.getPosX() - sprite.getWidth() / 2, enemy.getPosY() - sprite.getHeight() / 2);

                    if (enemy.getType() != EnemyType.TREE) {
                        float playerX = playerController.getPlayer().getPosX();
                        float enemyX = enemy.getPosX();

                        if (enemyX < playerX) {
                            sprite.setFlip(false, false);
                        } else {
                            sprite.setFlip(true, false);
                        }
                    }

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
