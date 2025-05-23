package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
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
    private float barrierWidth, barrierHeight;
    private float shrinkRate = 15f; // Slower shrink rate
    private float minSize = 20f;
    private boolean active = true;
    private Rectangle boundingBox;
    private Animation<Texture> animation;
    private float animationTime = 0f;
    private Sprite sprite;

    // Horizontal animation properties
    private float electricOffset = 0f;
    private float electricSpeed = 100f;

    public ElectricBarrier(float x, float y, float mapWidth, float mapHeight) {
        this.posX = x;
        this.posY = y;

        // Create horizontal rectangular barriers
        this.barrierWidth = MathUtils.random(150f, 300f);
        this.barrierHeight = 40f; // Fixed height for horizontal barriers

        this.boundingBox = new Rectangle(x - barrierWidth / 2, y - barrierHeight / 2, barrierWidth, barrierHeight);

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
            animation = new Animation<>(0.1f, frames); // Faster animation for electric effect
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } catch (Exception e) {
            Gdx.app.error("ElectricBarrier", "Failed to load barrier animation: " + e.getMessage());
        }
    }

    private Texture createFallbackTexture() {
        // Create a simple electric-looking texture
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);

        // Create electric blue pattern
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                if ((x + y) % 4 == 0) {
                    pixmap.setColor(0.2f, 0.6f, 1.0f, 0.8f); // Electric blue
                } else {
                    pixmap.setColor(0.8f, 0.9f, 1.0f, 0.6f); // Light blue
                }
                pixmap.drawPixel(x, y);
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
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
            sprite.setSize(barrierWidth, barrierHeight);
            sprite.setPosition(posX - barrierWidth / 2, posY - barrierHeight / 2);

            // Electric blue-white color with pulsing effect
            float pulse = 0.6f + 0.4f * (float) Math.sin(animationTime * 6);
            sprite.setColor(0.3f, 0.7f, 1.0f, pulse);
        }
    }

    public void update(float delta) {
        if (!active) return;

        animationTime += delta;
        electricOffset += electricSpeed * delta;

        // Slowly shrink the barrier over time
        barrierWidth = Math.max(minSize, barrierWidth - shrinkRate * delta);
        barrierHeight = Math.max(minSize / 2, barrierHeight - (shrinkRate * 0.5f) * delta);

        // Update bounding box
        boundingBox.set(posX - barrierWidth / 2, posY - barrierHeight / 2, barrierWidth, barrierHeight);

        updateSpriteTransform();

        // Update animation frame
        if (animation != null) {
            Texture currentFrame = animation.getKeyFrame(animationTime, true);
            sprite.setTexture(currentFrame);
        }

        // Deactivate when too small
        if (barrierWidth <= minSize && barrierHeight <= minSize / 2) {
            active = false;
        }

        // Enhanced electric pulsing effect
        float electricPulse = 0.5f + 0.5f * (float) Math.sin(animationTime * 10);
        sprite.setColor(0.2f + electricPulse * 0.5f, 0.6f + electricPulse * 0.3f, 1.0f, 0.7f + electricPulse * 0.3f);
    }

    public boolean checkCollision(Player player) {
        if (!active || player == null) return false;

        Rectangle playerBounds = player.getBoundingBox();
        return boundingBox.overlaps(playerBounds);
    }

    public void draw() {
        if (active && sprite != null) {
            // Draw the main barrier
            sprite.draw(Main.getBatch());

            // Add horizontal electric effect overlay
            drawElectricEffect();
        }
    }

    private void drawElectricEffect() {
        if (sprite == null) return;

        // Draw moving horizontal electric lines
        float lineHeight = 2f;
        int numLines = 3;

        for (int i = 0; i < numLines; i++) {
            float lineY = posY - barrierHeight / 2 + (i + 1) * (barrierHeight / (numLines + 1));
            float offsetX = (electricOffset + i * 30) % (barrierWidth + 40) - 20;

            // Create electric line effect
            Main.getBatch().setColor(1.0f, 1.0f, 1.0f, 0.8f);

            // Draw electric line segments
            for (float x = offsetX; x < barrierWidth; x += 20) {
                if (x >= 0 && x <= barrierWidth - 10) {
                    float lineX = posX - barrierWidth / 2 + x;
                    // Use a small white rectangle to simulate electric line
                    sprite.setPosition(lineX, lineY - lineHeight / 2);
                    sprite.setSize(10, lineHeight);
                    sprite.setColor(1.0f, 1.0f, 1.0f, 0.9f);
                    sprite.draw(Main.getBatch());
                }
            }
        }

        // Restore original sprite properties
        updateSpriteTransform();
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

