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
    private float initialSpawnRate = 3.0f;
    private float currentSpawnRate;
    private float minimumSpawnRate = 0.5f;
    private float spawnRateDecreasePerMinute = 0.2f;
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

        // Initialize with proper game state restoration
        initializeEnemyState();
    }

    private void initializeEnemyState() {
        if (App.getGame() != null && App.getGame().getGameTime() > 0) {
            // This is a loaded game - restore saved state
            this.gameTime = App.getGame().getGameTime();

            // Update spawn timers based on loaded game time
            this.lastTentacleSpawnTime = (float) (Math.floor(gameTime / 3.0f) * 3.0f);
            this.lastEyeBatSpawnTime = (float) (Math.floor(gameTime / 10.0f) * 10.0f);

            // Check if Elder Boss should have spawned
            float halfwayPoint = totalGameTimeLimit / 2.0f;
            if (gameTime >= halfwayPoint) {
                elderBossSpawned = true;
            }

            // Restore saved enemies with proper state
            restoreSavedEnemies();

            // Check if trees were restored from saved game
            boolean treesFound = false;
            for (Enemy enemy : enemies) {
                if (enemy.getType() == com.untilDawn.models.enums.EnemyType.TREE) {
                    treesFound = true;
                    break;
                }
            }

            // Only set treesPlaced to true if trees were actually restored
            this.treesPlaced = treesFound;

            Gdx.app.log("EnemyController", "Restored game state - Time: " + gameTime +
                ", Enemies: " + enemies.size() + ", Elder Boss: " + elderBossSpawned +
                ", Trees found: " + treesFound);
        } else {
            // New game - start fresh
            this.enemies = new ArrayList<>();
            this.gameTime = 0;
            this.elderBossSpawned = false;
            this.treesPlaced = false; // Trees will be placed in update()

            Gdx.app.log("EnemyController", "Starting new game");
        }
    }

    private void restoreSavedEnemies() {
        if (App.getGame() == null || App.getGame().getEnemies() == null) {
            this.enemies = new ArrayList<>();
            return;
        }

        this.enemies = new ArrayList<>();
        int treeCount = 0;
        int activeEnemyCount = 0;

        for (Enemy savedEnemy : App.getGame().getEnemies()) {
            if (savedEnemy != null) {
                // Always restore trees, regardless of active state
                boolean isTree = savedEnemy.getType() == com.untilDawn.models.enums.EnemyType.TREE;

                // Create new enemy instance with proper state restoration
                // For trees or active enemies
                if (isTree || savedEnemy.isActive()) {
                    Enemy restoredEnemy = restoreEnemyState(savedEnemy);
                    if (restoredEnemy != null) {
                        this.enemies.add(restoredEnemy);

                        if (isTree) {
                            treeCount++;
                            Gdx.app.log("EnemyController", "Restored tree at position: (" +
                                restoredEnemy.getPosX() + ", " + restoredEnemy.getPosY() + ")");
                        }

                        if (restoredEnemy.isActive()) {
                            activeEnemyCount++;
                        }

                        // Check if this is an Elder Boss
                        if (restoredEnemy instanceof ElderBoss) {
                            this.elderBoss = (ElderBoss) restoredEnemy;
                            this.elderBossSpawned = true;
                            Gdx.app.log("EnemyController", "Restored Elder Boss");
                        }
                    }
                }
            }
        }

        Gdx.app.log("EnemyController", "Restored " + enemies.size() + " enemies from save " +
            "(Trees: " + treeCount + ", Active enemies: " + activeEnemyCount + ")");
    }

    private Enemy restoreEnemyState(Enemy savedEnemy) {
        try {
            Enemy restoredEnemy;

            // Create the appropriate enemy type
            if (savedEnemy instanceof ElderBoss) {
                restoredEnemy = new ElderBoss(savedEnemy.getPosX(), savedEnemy.getPosY(), mapWidth, mapHeight);
                // Mark that Elder Boss has been spawned
                this.elderBoss = (ElderBoss) restoredEnemy;
                this.elderBossSpawned = true;
            } else {
                restoredEnemy = new Enemy(savedEnemy.getType(), savedEnemy.getPosX(), savedEnemy.getPosY());
            }

            // Restore common enemy state
            restoreCommonEnemyState(restoredEnemy, savedEnemy);

            // For trees, mark them as already placed
            if (savedEnemy.getType() == EnemyType.TREE) {
                this.treesPlaced = true;
            }

            return restoredEnemy;

        } catch (Exception e) {
            Gdx.app.error("EnemyController", "Failed to restore enemy: " + e.getMessage());
            return null;
        }
    }

    private void restoreCommonEnemyState(Enemy newEnemy, Enemy savedEnemy) {
        // Apply damage to match saved health
        int healthDiff = savedEnemy.getType().getHealth() - savedEnemy.getHealth();
        for (int i = 0; i < healthDiff && newEnemy.isActive(); i++) {
            newEnemy.hit(1); // Apply damage without killing
        }

        // Restore position (should already be set, but ensure it's exact)
        newEnemy.setPosX(savedEnemy.getPosX());
        newEnemy.setPosY(savedEnemy.getPosY());

        // Update sprite position to match
        if (newEnemy.getSprite() != null) {
            newEnemy.getSprite().setPosition(
                newEnemy.getPosX() - newEnemy.getSprite().getWidth() / 2,
                newEnemy.getPosY() - newEnemy.getSprite().getHeight() / 2
            );
        }
    }

    public void update(float delta) {
        Map<String, String> keyBindings = App.getKeybinds();
        if (autoAimCooldown <= 0 && Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBindings.get("Auto Aim")))) {
            autoAim = !autoAim;
            autoAimCooldown = 0.2f;
        } else {
            autoAimCooldown -= delta;
        }

        gameTime += delta;

        // Update game time in the App for saving
        if (App.getGame() != null) {
            App.getGame().setGameTime(gameTime);
        }

        // Place trees if they haven't been placed yet
        // This handles both new games and loaded games where trees weren't saved
        if (!treesPlaced) {
            // Check if we need to place trees
            boolean needToPlaceTrees = true;

            // For loaded games, only place trees if we're in the initial state
            if (App.getGame() != null && App.getGame().getGameTime() > 0.1f) {
                // Count existing trees
                int treeCount = 0;
                for (Enemy enemy : enemies) {
                    if (enemy.getType() == com.untilDawn.models.enums.EnemyType.TREE) {
                        treeCount++;
                    }
                }

                // If we already have trees, don't place more
                if (treeCount > 0) {
                    needToPlaceTrees = false;
                    treesPlaced = true;
                    Gdx.app.log("EnemyController", "Trees already exist (" + treeCount + "), not placing new ones");
                }
            }

            if (needToPlaceTrees) {
                placeTrees();
            }
        }

        updateSpawnRate();
        checkElderBossSpawn();

        // Only spawn new enemies if not in a loaded game's initial state
        if (gameTime > 0.1f) { // Small buffer to ensure proper initialization
            spawnTentacleMonsters();
            spawnEyeBats();
        }

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
        float tentacleSpawnInterval = 3.0f;
        if (gameTime >= tentacleSpawnInterval && (gameTime - lastTentacleSpawnTime) >= tentacleSpawnInterval) {
            int tentaclesToSpawn = Math.max(1, (int) Math.floor(gameTime / 30.0f));

            for (int i = 0; i < tentaclesToSpawn; i++) {
                Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);
                Enemy enemy = new Enemy(EnemyType.TENTACLE, spawnPos.x, spawnPos.y);
                enemies.add(enemy);
            }

            lastTentacleSpawnTime = gameTime;
            Gdx.app.log("EnemyController", "Spawned " + tentaclesToSpawn + " tentacle monsters at time " + gameTime);
        }
    }

    private void spawnEyeBats() {
        float eyeBatStartTime = totalGameTimeLimit / 4.0f;
        float eyeBatSpawnInterval = 10.0f;

        if (gameTime >= eyeBatStartTime && (gameTime - lastEyeBatSpawnTime) >= eyeBatSpawnInterval) {
            float spawnRate = (4 * gameTime - totalGameTimeLimit + 30) / 30.0f;
            int eyeBatsToSpawn = Math.max(0, (int) Math.floor(spawnRate));

            if (spawnRate > 0 && eyeBatsToSpawn < 1) {
                eyeBatsToSpawn = 1;
            }

            if (eyeBatsToSpawn > 0) {
                for (int i = 0; i < eyeBatsToSpawn; i++) {
                    Vector2 spawnPos = Enemy.getRandomSpawnPosition(mapWidth, mapHeight, 50);
                    Enemy enemy = new Enemy(EnemyType.EYEBAT, spawnPos.x, spawnPos.y);
                    enemies.add(enemy);
                }

                Gdx.app.log("EnemyController", "Spawned " + eyeBatsToSpawn + " eye bats at time " + gameTime);
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
        Gdx.app.log("EnemyController", "Placed " + numberOfTrees + " trees");
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

            // Remove enemy only after death animation is complete
            if (!enemy.isActive() && !enemy.isDropActive() && !enemy.isDeathAnimationPlaying()) {
                if (enemy.isDeathAnimationComplete()) {
                    enemy.dispose();
                    iterator.remove();
                }
            }
        }
    }

    // ... Rest of the methods remain the same as in the original code ...
    // (checkBulletCollisions, checkEnemyBulletCollisions, drawEnemyBullets, etc.)

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
                        player.startCurseAnimation();
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
                    player.setPlayerHealth(player.getPlayerHealth() - 1);
                    player.setInvincible(true, 1.0f);
                    player.startCurseAnimation();
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
            // Draw death animation if playing
            if (enemy.isDeathAnimationPlaying()) {
                Texture deathFrame = enemy.getDeathAnimationFrame();
                if (deathFrame != null) {
                    float deathScale = 2.0f;
                    float deathX = enemy.getDeathPosX() - (deathFrame.getWidth() * deathScale) / 2;
                    float deathY = enemy.getDeathPosY() - (deathFrame.getHeight() * deathScale) / 2;

                    float alpha = 1.0f - (enemy.getDeathAnimTimer() / enemy.getDeathAnimation().getAnimationDuration()) * 0.3f;
                    Main.getBatch().setColor(1f, 1f, 1f, alpha);

                    Main.getBatch().draw(deathFrame, deathX, deathY,
                        deathFrame.getWidth() * deathScale, deathFrame.getHeight() * deathScale);

                    Main.getBatch().setColor(1f, 1f, 1f, 1f);
                }
                continue;
            }

            // Draw living enemies
            if (enemy.isActive()) {
                if (enemy instanceof ElderBoss boss) {
                    Sprite sprite = boss.getSprite();
                    float scale = 3.0f;
                    sprite.setSize(sprite.getTexture().getWidth() * scale, sprite.getTexture().getHeight() * scale);
                    sprite.setPosition(boss.getPosX() - sprite.getWidth() / 2, boss.getPosY() - sprite.getHeight() / 2);

                    float playerX = playerController.getPlayer().getPosX();
                    float bossX = boss.getPosX();

                    sprite.setFlip(bossX > playerX, false);
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
                            sprite.setFlip(enemyX > playerX, false);
                        }

                        sprite.draw(Main.getBatch());
                    }
                }
            } else if (enemy.isDropActive() && enemy.getDropSprite() != null) {
                enemy.getDropSprite().draw(Main.getBatch());
            }
        }
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

    // Getters
    public ArrayList<Enemy> getEnemies() {
        return enemies;
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
