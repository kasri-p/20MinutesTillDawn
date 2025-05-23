package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;
import com.untilDawn.Main;
import com.untilDawn.models.*;
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
    private float mapWidth;
    private float mapHeight;
    private boolean treesPlaced = false;
    private boolean autoAim = false;
    private float autoAimCooldown = 0;

    private Circle bulletCircle = new Circle();
    private Circle enemyCircle = new Circle();
    private Vector2 bulletVelocity = new Vector2();
    private Vector2 prevBulletPos = new Vector2();
    private Vector2 currentBulletPos = new Vector2();

    private float lastTentacleSpawnTime = 0;
    private float lastEyeBatSpawnTime = 0;
    private float totalGameTimeLimit;

    // Elder Boss fields
    private ElderBoss elderBoss;
    private boolean elderBossSpawned = false;

    public EnemyController(PlayerController playerController, WeaponController weaponController, float mapWidth, float mapHeight) {
        this.playerController = playerController;
        this.weaponController = weaponController;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.currentSpawnRate = initialSpawnRate;

        this.totalGameTimeLimit = App.getGame() != null ? App.getGame().getTimeLimit() * 60 : 300;
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

        // Check for Elder Boss spawn
        checkElderBossSpawn();

        spawnTentacleMonsters();
        spawnEyeBats();

        updateEnemies(delta);

        checkBulletCollisions(delta);

        checkEnemyBulletCollisions(delta);

        checkPlayerCollisions();

        drawEnemies();

        drawEnemyBullets();

        checkAutoAim();
    }

    private void checkElderBossSpawn() {
        // Check if we've reached the halfway point of the game
        float halfwayPoint = totalGameTimeLimit / 2.0f;

        if (!elderBossSpawned && gameTime >= halfwayPoint) {
            spawnElderBoss();
            elderBossSpawned = true;
        }
    }

    private void spawnElderBoss() {
        Vector2 spawnPos = getElderBossSpawnPosition();
        elderBoss = new ElderBoss(spawnPos.x, spawnPos.y, mapWidth, mapHeight);
        enemies.add(elderBoss);

        Gdx.app.log("EnemyController", "Elder Boss spawned at halfway point! Position: (" +
            spawnPos.x + ", " + spawnPos.y + ")");

        GameAssetManager.getGameAssetManager().playElderSpawn();
    }

    public void forceSpawnElderBoss() {
        if (!elderBossSpawned) {
            spawnElderBoss();
            elderBossSpawned = true;
        }
    }

    private Vector2 getElderBossSpawnPosition() {
        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float minDistance = 300f;
        float maxAttempts = 10;

        for (int i = 0; i < maxAttempts; i++) {
            float angle = MathUtils.random(0, MathUtils.PI2);
            float distance = minDistance + MathUtils.random(0, 200f);

            float spawnX = playerX + MathUtils.cos(angle) * distance;
            float spawnY = playerY + MathUtils.sin(angle) * distance;

            spawnX = MathUtils.clamp(spawnX, 100, mapWidth - 100);
            spawnY = MathUtils.clamp(spawnY, 100, mapHeight - 100);

            float actualDistance = Vector2.dst(spawnX, spawnY, playerX, playerY);
            if (actualDistance >= minDistance) {
                return new Vector2(spawnX, spawnY);
            }
        }

        return new Vector2(mapWidth / 2, mapHeight / 2);
    }

    private void spawnTentacleMonsters() {
        // Every 3 seconds
        float tentacleSpawnInterval = 3.0f;
        if (gameTime >= tentacleSpawnInterval && (gameTime - lastTentacleSpawnTime) >= tentacleSpawnInterval) {
            int tentaclesToSpawn = (int) Math.floor(gameTime / 30.0f);

            if (tentaclesToSpawn < 1) {
                tentaclesToSpawn = 1;
            }

            for (int i = 0; i < tentaclesToSpawn; i++) {
                Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);
                Enemy enemy = new Enemy(EnemyType.TENTACLE, spawnPos.x, spawnPos.y);
                enemies.add(enemy);
            }

            lastTentacleSpawnTime = gameTime;

            System.out.println("Spawned " + tentaclesToSpawn + " tentacle monsters at time " + gameTime);
        }
    }

    private void spawnEyeBats() {
        float eyeBatStartTime = totalGameTimeLimit / 4.0f;

        // Every 10 seconds
        float eyeBatSpawnInterval = 10.0f;
        if (gameTime >= eyeBatStartTime && (gameTime - lastEyeBatSpawnTime) >= eyeBatSpawnInterval) {
            float spawnRate = (4 * gameTime - totalGameTimeLimit + 30) / 30.0f;
            int eyeBatsToSpawn = (int) Math.floor(spawnRate);

            if (spawnRate > 0 && eyeBatsToSpawn < 1) {
                eyeBatsToSpawn = 1;
            }

            if (eyeBatsToSpawn > 0) {
                for (int i = 0; i < eyeBatsToSpawn; i++) {
                    Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);
                    Enemy enemy = new Enemy(EnemyType.EYEBAT, spawnPos.x, spawnPos.y);
                    enemies.add(enemy);
                }

                System.out.println("Spawned " + eyeBatsToSpawn + " eye bats at time " + gameTime + " (rate: " + spawnRate + ")");
            }

            lastEyeBatSpawnTime = gameTime;
        }
    }

    private void placeTrees() {
        ArrayList<Circle> treePositions = new ArrayList<>();

        int numberOfTrees = 30;
        for (int i = 0; i < numberOfTrees; i++) {
            float x = MathUtils.random(100, mapWidth - 100);
            float y = MathUtils.random(100, mapHeight - 100);

            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            float distanceToPlayer = Vector2.dst(x, y, playerX, playerY);

            float treeRadius = 75f;
            boolean tooCloseToOtherTree = false;

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
                } else if (enemy.getType() == EnemyType.ELDER) {
                    // Elder boss has larger collision radius
                    enemyRadius = Math.max(boundingBox.width, boundingBox.height) / 2.0f;
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
                    System.out.println("enemy hit by damage " + bullet.getDamage());
                    bullet.setActive(false);

                    break;
                }
            }
        }
    }

    private void checkEnemyBulletCollisions(float delta) {
        Player player = playerController.getPlayer();
        if (player == null) return;

        float playerX = player.getPosX();
        float playerY = player.getPosY();
        float playerRadius = Math.min(player.getPlayerSprite().getWidth(), player.getPlayerSprite().getHeight()) / 3.0f;
        Circle playerCircle = new Circle(playerX, playerY, playerRadius);

        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) continue;

            ArrayList<Enemy.EnemyBullet> enemyBullets = enemy.getBullets();
            Iterator<Enemy.EnemyBullet> bulletIterator = enemyBullets.iterator();

            while (bulletIterator.hasNext()) {
                Enemy.EnemyBullet bullet = bulletIterator.next();

                if (!bullet.isActive()) {
                    bullet.dispose();
                    bulletIterator.remove();
                    continue;
                }

                if (Intersector.overlaps(playerCircle, bullet.getCollisionCircle())) {
                    if (!player.isInvincible()) {
                        player.takeDamage(1);
                        player.setInvincible(true, 1.0f);
                    }

                    bullet.setActive(false);
                    bullet.dispose();
                    bulletIterator.remove();
                }
            }
        }
    }

    private void drawEnemyBullets() {
        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) continue;

            for (Enemy.EnemyBullet bullet : enemy.getBullets()) {
                if (bullet.isActive()) {
                    bullet.getSprite().draw(Main.getBatch());
                }
            }
        }
    }

    private void checkPlayerCollisions() {
        if (playerController == null || playerController.getPlayer() == null) return;

        Player player = playerController.getPlayer();
        Sprite playerSprite = player.getPlayerSprite();
        Rectangle playerRect = playerSprite.getBoundingRectangle();

        float playerX = player.getPosX();
        float playerY = player.getPosY();

        float playerRadius = Math.min(playerRect.width, playerRect.height) / 3.0f;

        Circle playerCircle = new Circle(playerX, playerY, playerRadius);

        for (Enemy enemy : enemies) {
            Rectangle enemyRect = enemy.getBoundingBox();
            float enemyX = enemy.getPosX();
            float enemyY = enemy.getPosY();

            float enemyRadius;

            if (enemy.getType() == EnemyType.TREE) {
                enemyRadius = Math.max(enemyRect.width, enemyRect.height) / 3.0f;
            } else if (enemy.getType() == EnemyType.ELDER) {
                enemyRadius = Math.max(enemyRect.width, enemyRect.height) / 2.0f;
            } else {
                enemyRadius = Math.max(enemyRect.width, enemyRect.height) / 2.5f;
            }

            Circle enemyCircle = new Circle(enemyX, enemyY, enemyRadius);

            if (enemy.isActive() && Intersector.overlaps(playerCircle, enemyCircle)) {
                if (!player.isInvincible()) {
                    Gdx.app.log("Player Health", "Player health: " + player.getPlayerHealth());

                    player.setPlayerHealth(player.getPlayerHealth() - 1);
                    player.setInvincible(true, 1.0f); // 1 second of invincibility

                    Gdx.app.log("Collision", "Player collided with enemy: " + enemy.getType().getName());
                }
                return;
            } else if (!enemy.isActive() && enemy.isDropActive()) {
                if (Intersector.overlaps(playerCircle, enemyCircle)) {
                    enemy.collectDrop(player);
                }
            }
        }
    }

    private void drawEnemies() {
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();

        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                if (enemy instanceof ElderBoss boss) {
                    Sprite sprite = boss.getSprite();

                    float scale = 3.0f;
                    sprite.setSize(sprite.getTexture().getWidth() * scale,
                        sprite.getTexture().getHeight() * scale);
                    sprite.setPosition(boss.getPosX() - sprite.getWidth() / 2,
                        boss.getPosY() - sprite.getHeight() / 2);

                    float playerX = playerController.getPlayer().getPosX();
                    float bossX = boss.getPosX();

                    if (bossX < playerX) {
                        sprite.setFlip(false, false);
                    } else {
                        sprite.setFlip(true, false);
                    }

                    sprite.setColor(1.0f, 0.9f, 0.8f, 1.0f);
                    sprite.draw(Main.getBatch());

                    
                } else {
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

        if (elderBoss != null) {
            elderBoss.dispose();
            elderBoss = null;
        }
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
                Vector3 enemyWorldPos = new Vector3(closestEnemy.getPosX(), closestEnemy.getPosY(), 0);
                Vector3 screenCoords = weaponController.getCamera().project(enemyWorldPos);

                int cursorX = (int) screenCoords.x;
                int cursorY = Gdx.graphics.getHeight() - (int) screenCoords.y;

                Gdx.input.setCursorPosition(cursorX, cursorY);
            }
        }
    }

    public boolean isElderBossSpawned() {
        return elderBossSpawned;
    }

    public ElderBoss getElderBoss() {
        return elderBoss;
    }

    public float getGameTime() {
        return gameTime;
    }

    public float getTotalGameTimeLimit() {
        return totalGameTimeLimit;
    }

    public int getActiveEnemyCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                count++;
            }
        }
        return count;
    }
}
