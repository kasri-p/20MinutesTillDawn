package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.Main;
import com.untilDawn.models.enums.EnemyType;

import java.util.ArrayList;


public class ElderBoss extends Enemy {
    // Boss mechanics
    private ElderState currentState = ElderState.WALKING;
    private float dashCooldown = 5.0f; // Dash every 5 seconds
    private float dashTimer = 0f;
    private float stateTimer = 0f;
    private boolean isDashing = false;
    private Vector2 dashDirection = new Vector2();
    private Vector2 dashStartPos = new Vector2();
    private float dashSpeed = 8.0f;
    private float dashDistance = 400f;
    private float chargeDuration = 1.5f;
    private float attackDuration = 0.8f;

    // Barriers
    private ArrayList<ElectricBarrier> barriers = new ArrayList<>();
    private boolean barriersActive = false;
    private float barrierSpawnTimer = 0f;
    private float barrierSpawnInterval = 2f;
    private float mapWidth;
    private float mapHeight;

    // Animations
    private Animation<Texture> walkAnimation;
    private Animation<Texture> chargeAnimation;
    private Animation<Texture> attackAnimation;
    private float animationTime = 0f;

    public ElderBoss(float posX, float posY, float mapWidth, float mapHeight) {
        super(EnemyType.ELDER, posX, posY);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        loadAnimations();
        initializeBarriers();

        currentState = ElderState.WALKING;
        dashTimer = dashCooldown;

        Gdx.app.log("ElderBoss", "Elder Boss spawned at (" + posX + ", " + posY + ") with " + getHealth() + " HP");
    }

    private void loadAnimations() {
        try {
            Texture[] walkFrames = new Texture[3];
            for (int i = 0; i < 3; i++) {
                walkFrames[i] = new Texture(Gdx.files.internal("Images/Enemies/elder/elder" + i + ".png"));
            }
            walkAnimation = new Animation<>(0.3f, walkFrames);
            walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

            Texture[] chargeFrames = new Texture[5];
            for (int i = 0; i < 5; i++) {
                chargeFrames[i] = new Texture(Gdx.files.internal("Images/Enemies/elder/elder" + (i + 3) + ".png"));
            }
            chargeAnimation = new Animation<>(0.2f, chargeFrames);
            chargeAnimation.setPlayMode(Animation.PlayMode.LOOP);

            Texture[] attackFrames = new Texture[3];
            for (int i = 0; i < 3; i++) {
                attackFrames[i] = new Texture(Gdx.files.internal("Images/Enemies/elder/elder" + (i + 8) + ".png"));
            }
            attackAnimation = new Animation<>(0.15f, attackFrames);
            attackAnimation.setPlayMode(Animation.PlayMode.LOOP);

        } catch (Exception e) {
            Gdx.app.error("ElderBoss", "Failed to load elder animations: " + e.getMessage());
        }
    }

    private void initializeBarriers() {
        barriers = new ArrayList<>();
        barriersActive = true;
        createInitialBarriers();
    }

    private void createInitialBarriers() {
        int numBarriers = 8;
        float screenPerimeter = 2 * (mapWidth + mapHeight);
        float barrierSpacing = screenPerimeter / numBarriers;

        for (int i = 0; i < numBarriers; i++) {
            float position = i * barrierSpacing;
            Vector2 barrierPos = getPositionOnPerimeter(position);
            barriers.add(new ElectricBarrier(barrierPos.x, barrierPos.y, mapWidth, mapHeight));
        }
    }

    private Vector2 getPositionOnPerimeter(float distance) {
        float mapPerimeter = 2 * (mapWidth + mapHeight);
        distance = distance % mapPerimeter;

        if (distance < mapWidth) {
            return new Vector2(distance, mapHeight);
        } else if (distance < mapWidth + mapHeight) {
            return new Vector2(mapWidth, mapHeight - (distance - mapWidth));
        } else if (distance < 2 * mapWidth + mapHeight) {
            return new Vector2(mapWidth - (distance - mapWidth - mapHeight), 0);
        } else {
            return new Vector2(0, distance - 2 * mapWidth - mapHeight);
        }
    }

    @Override
    public void update(float delta, Player player) {
        if (!isActive()) return;

        animationTime += delta;
        stateTimer += delta;
        dashTimer += delta;
        barrierSpawnTimer += delta;

        updateBossLogic(delta, player);
        updateBarriers(delta, player);
        updateAnimation();

        super.update(delta, player);
    }

    private void updateBossLogic(float delta, Player player) {
        switch (currentState) {
            case WALKING:
                updateWalkingState(delta, player);
                break;
            case CHARGING:
                updateChargingState(delta, player);
                break;
            case ATTACKING:
                updateAttackingState(delta, player);
                break;
        }
    }

    private void updateWalkingState(float delta, Player player) {
        if (player != null) {
            float playerX = player.getPosX();
            float playerY = player.getPosY();

            Vector2 direction = new Vector2(playerX - getPosX(), playerY - getPosY()).nor();

            float walkSpeed = getType().getSpeed() * 0.6f;
            setPosX(getPosX() + direction.x * walkSpeed * delta * 60);
            setPosY(getPosY() + direction.y * walkSpeed * delta * 60);

            if (dashTimer >= dashCooldown) {
                startCharging(player);
            }
        }
    }

    private void updateChargingState(float delta, Player player) {
        if (stateTimer >= chargeDuration) {
            startAttacking(player);
        }
    }

    private void updateAttackingState(float delta, Player player) {
        if (!isDashing) {
            isDashing = true;
            dashStartPos.set(getPosX(), getPosY());
        }

        float dashProgress = stateTimer / attackDuration;
        if (dashProgress < 1.0f) {
            float currentDashDistance = dashDistance * dashProgress;
            setPosX(dashStartPos.x + dashDirection.x * currentDashDistance);
            setPosY(dashStartPos.y + dashDirection.y * currentDashDistance);
        } else {
            finishAttack();
        }
    }

    private void startCharging(Player player) {
        currentState = ElderState.CHARGING;
        stateTimer = 0f;

        if (player != null) {
            dashDirection.set(player.getPosX() - getPosX(), player.getPosY() - getPosY()).nor();
        }

        Gdx.app.log("ElderBoss", "Elder Boss started charging!");
    }

    private void startAttacking(Player player) {
        currentState = ElderState.ATTACKING;
        stateTimer = 0f;
        isDashing = false;

        Gdx.app.log("ElderBoss", "Elder Boss is attacking!");
    }

    private void finishAttack() {
        currentState = ElderState.WALKING;
        stateTimer = 0f;
        dashTimer = 0f;
        isDashing = false;

        Gdx.app.log("ElderBoss", "Elder Boss finished attack, returning to walking");
    }

    private void updateBarriers(float delta, Player player) {
        if (!barriersActive) return;

        for (int i = barriers.size() - 1; i >= 0; i--) {
            ElectricBarrier barrier = barriers.get(i);
            barrier.update(delta);

            if (barrier.isActive() && player != null && !player.isInvincible()) {
                if (barrier.checkCollision(player)) {
                    player.takeDamage(1);
                    player.setInvincible(true, 1.0f);
                    Gdx.app.log("ElderBoss", "Player hit by electric barrier!");
                }
            }

            if (!barrier.isActive()) {
                barriers.remove(i);
            }
        }

        if (barrierSpawnTimer >= barrierSpawnInterval && barriers.size() < 12) {
            spawnRandomBarrier();
            barrierSpawnTimer = 0f;
        }
    }

    private void spawnRandomBarrier() {
        float x, y;
        int edge = MathUtils.random(3);

        switch (edge) {
            case 0: // Top
                x = MathUtils.random(0, mapWidth);
                y = mapHeight - 50;
                break;
            case 1: // Right
                x = mapWidth - 50;
                y = MathUtils.random(0, mapHeight);
                break;
            case 2: // Bottom
                x = MathUtils.random(0, mapWidth);
                y = 50;
                break;
            default: // Left
                x = 50;
                y = MathUtils.random(0, mapHeight);
                break;
        }

        barriers.add(new ElectricBarrier(x, y, mapWidth, mapHeight));
    }

    private void updateAnimation() {
        Animation<Texture> currentAnimation = switch (currentState) {
            case WALKING -> walkAnimation;
            case CHARGING -> chargeAnimation;
            case ATTACKING -> attackAnimation;
        };

        if (currentAnimation != null) {
            Texture currentFrame = currentAnimation.getKeyFrame(animationTime, true);
            getSprite().setTexture(currentFrame);
        }
    }

    public void drawBarriers() {
        for (ElectricBarrier barrier : barriers) {
            barrier.draw();
        }
    }

    @Override
    public boolean hit(int damage) {
        boolean killed = super.hit(damage);

        if (killed) {
            barriersActive = false;
            for (ElectricBarrier barrier : barriers) {
                barrier.setActive(false);
            }
            barriers.clear();
            Gdx.app.log("ElderBoss", "Elder Boss defeated! All barriers removed.");
        }

        return killed;
    }

    @Override
    public void dispose() {
        super.dispose();

        for (ElectricBarrier barrier : barriers) {
            barrier.dispose();
        }
        barriers.clear();

        if (walkAnimation != null) {
            for (Texture frame : walkAnimation.getKeyFrames()) {
                if (frame != null) frame.dispose();
            }
        }
        if (chargeAnimation != null) {
            for (Texture frame : chargeAnimation.getKeyFrames()) {
                if (frame != null) frame.dispose();
            }
        }
        if (attackAnimation != null) {
            for (Texture frame : attackAnimation.getKeyFrames()) {
                if (frame != null) frame.dispose();
            }
        }
    }

    public ElderState getCurrentState() {
        return currentState;
    }

    public float getDashCooldownRemaining() {
        return Math.max(0, dashCooldown - dashTimer);
    }

    public int getActiveBarrierCount() {
        return barriers.size();
    }

    public boolean isBarriersActive() {
        return barriersActive;
    }

    // Boss states
    private enum ElderState {
        WALKING,
        CHARGING,
        ATTACKING
    }
}

class ElectricBarrier {
    private float posX, posY;
    private float initialWidth, initialHeight;
    private float currentWidth, currentHeight;
    private float shrinkRate = 20f; // Units per second
    private float minSize = 30f;
    private boolean active = true;
    private Rectangle boundingBox;
    private Animation<Texture> animation;
    private float animationTime = 0f;
    private Sprite sprite;

    public ElectricBarrier(float x, float y, float mapWidth, float mapHeight) {
        this.posX = x;
        this.posY = y;

        float distanceFromCenter = Vector2.dst(x, y, mapWidth / 2, mapHeight / 2);
        float maxDistance = Vector2.dst(0, 0, mapWidth / 2, mapHeight / 2);
        float sizeMultiplier = 0.5f + (distanceFromCenter / maxDistance) * 1.5f;

        this.initialWidth = 100f * sizeMultiplier;
        this.initialHeight = 60f * sizeMultiplier;
        this.currentWidth = initialWidth;
        this.currentHeight = initialHeight;

        this.boundingBox = new Rectangle(x - currentWidth / 2, y - currentHeight / 2, currentWidth, currentHeight);

        loadAnimation();
        createSprite();
    }

    private void loadAnimation() {
        try {
            Texture[] frames = new Texture[6];
            for (int i = 0; i < 6; i++) {
                String framePath = "Images/ElectricWall/T_ElectricWall" + (i + 1) + ".png";
                if (Gdx.files.internal(framePath).exists()) {
                    frames[i] = new Texture(Gdx.files.internal(framePath));
                } else {
                    frames[i] = createFallbackTexture();
                }
            }
            animation = new Animation<>(0.15f, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } catch (Exception e) {
            Gdx.app.error("ElectricBarrier", "Failed to load barrier animation: " + e.getMessage());
        }
    }

    private Texture createFallbackTexture() {
        return new Texture(Gdx.files.internal("Images/bullet.png")); // Reuse existing texture
    }

    private void createSprite() {
        if (animation != null) {
            Texture firstFrame = animation.getKeyFrame(0);
            sprite = new Sprite(firstFrame);
            updateSpriteTransform();
        }
    }

    private void updateSpriteTransform() {
        if (sprite != null) {
            sprite.setSize(currentWidth, currentHeight);
            sprite.setPosition(posX - currentWidth / 2, posY - currentHeight / 2);
            sprite.setColor(1f, 0.8f, 0.2f, 0.9f); // Electric blue-yellow color
        }
    }

    public void update(float delta) {
        if (!active) return;

        animationTime += delta;

        currentWidth = Math.max(minSize, currentWidth - shrinkRate * delta);
        currentHeight = Math.max(minSize, currentHeight - shrinkRate * delta);

        boundingBox.set(posX - currentWidth / 2, posY - currentHeight / 2, currentWidth, currentHeight);

        updateSpriteTransform();

        if (animation != null) {
            Texture currentFrame = animation.getKeyFrame(animationTime, true);
            sprite.setTexture(currentFrame);
        }

        if (currentWidth <= minSize && currentHeight <= minSize) {
            active = false;
        }

        float pulse = 0.7f + 0.3f * (float) Math.sin(animationTime * 8);
        sprite.setColor(1f, 0.8f, 0.2f, pulse);
    }

    public boolean checkCollision(Player player) {
        if (!active || player == null) return false;

        Rectangle playerBounds = player.getBoundingBox();
        return boundingBox.overlaps(playerBounds);
    }

    public void draw() {
        if (active && sprite != null) {
            sprite.draw(Main.getBatch());
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void dispose() {
        if (animation != null) {
            for (Texture frame : animation.getKeyFrames()) {
                if (frame != null) frame.dispose();
            }
        }
    }
}
