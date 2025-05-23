package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.Main;
import com.untilDawn.models.enums.EnemyType;

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

    // Single large barrier
    private ElectricBarrier perimeterBarrier;
    private boolean barrierActive = false;
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
        initializeBarrier();

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

    private void initializeBarrier() {
        perimeterBarrier = new ElectricBarrier(mapWidth, mapHeight);
        barrierActive = true;
    }

    @Override
    public void update(float delta, Player player) {
        if (!isActive()) return;

        animationTime += delta;
        stateTimer += delta;
        dashTimer += delta;

        updateBossLogic(delta, player);
        updateBarrier(delta, player);
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

    private void updateBarrier(float delta, Player player) {
        if (!barrierActive || perimeterBarrier == null) return;

        perimeterBarrier.update(delta);

        if (player != null && !player.isInvincible()) {
            if (perimeterBarrier.checkCollision(player)) {
                player.takeDamage(1);
                player.setInvincible(true, 1.0f);
                Gdx.app.log("ElderBoss", "Player hit by electric barrier!");
            }
        }
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

    public void drawBarrier() {
        if (barrierActive && perimeterBarrier != null) {
            perimeterBarrier.draw();
        }
    }

    @Override
    public boolean hit(int damage) {
        boolean killed = super.hit(damage);

        if (killed) {
            barrierActive = false;
            if (perimeterBarrier != null) {
                perimeterBarrier.setActive(false);
            }
            Gdx.app.log("ElderBoss", "Elder Boss defeated! Barrier removed.");
        }

        return killed;
    }

    @Override
    public void dispose() {
        super.dispose();

        if (perimeterBarrier != null) {
            perimeterBarrier.dispose();
        }

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

    public boolean isBarrierActive() {
        return barrierActive;
    }

    // Boss states
    private enum ElderState {
        WALKING,
        CHARGING,
        ATTACKING
    }
}

class ElectricBarrier {
    private float mapWidth, mapHeight;
    private float thickness = 30f; // Thickness of the barrier
    private boolean active = true;
    private Rectangle[] boundingBoxes; // One for each side of the map
    private Animation<Texture> animation;
    private float animationTime = 0f;
    private Sprite[] sprites; // One for each side of the map
    private float electricPulseSpeed = 8f;
    private float electricIntensity = 0f;

    public ElectricBarrier(float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        initializeBarrier();
        loadAnimation();
        createSprites();
    }

    private void initializeBarrier() {
        boundingBoxes = new Rectangle[4];
        // Top barrier
        boundingBoxes[0] = new Rectangle(0, mapHeight - thickness, mapWidth, thickness);
        // Right barrier
        boundingBoxes[1] = new Rectangle(mapWidth - thickness, 0, thickness, mapHeight);
        // Bottom barrier
        boundingBoxes[2] = new Rectangle(0, 0, mapWidth, thickness);
        // Left barrier
        boundingBoxes[3] = new Rectangle(0, 0, thickness, mapHeight);
    }

    private void loadAnimation() {
        try {
            Texture[] frames = new Texture[6];
            for (int i = 1; i < 7; i++) {
                frames[i - 1] = new Texture(Gdx.files.internal("Images/ElectricWall/T_ElectricWall" + i + ".png"));
            }
            animation = new Animation<>(0.1f, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } catch (Exception e) {
            Gdx.app.error("ElectricBarrier", "Failed to load electric barrier animation: " + e.getMessage());
        }
    }

    private void createSprites() {
        sprites = new Sprite[4];
        Texture firstFrame = animation != null ? animation.getKeyFrame(0) : createSolidElectricTexture();

        // Create sprites for each side
        for (int i = 0; i < 4; i++) {
            sprites[i] = new Sprite(firstFrame);
            updateSprite(i);
        }
    }

    private Texture createSolidElectricTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        // Create electric blue texture
        pixmap.setColor(0.2f, 0.5f, 1f, 0.8f);
        pixmap.fill();
        // Add some electric pattern
        pixmap.setColor(0.8f, 0.9f, 1f, 1f);
        for (int i = 0; i < 32; i += 4) {
            pixmap.drawLine(i, 0, i, 32);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void updateSprite(int index) {
        if (sprites[index] == null) return;

        switch (index) {
            case 0: // Top
                sprites[index].setSize(mapWidth, thickness);
                sprites[index].setPosition(0, mapHeight - thickness);
                break;
            case 1: // Right
                sprites[index].setSize(thickness, mapHeight);
                sprites[index].setPosition(mapWidth - thickness, 0);
                break;
            case 2: // Bottom
                sprites[index].setSize(mapWidth, thickness);
                sprites[index].setPosition(0, 0);
                break;
            case 3: // Left
                sprites[index].setSize(thickness, mapHeight);
                sprites[index].setPosition(0, 0);
                break;
        }

        // Electric blue color with pulsing effect
        sprites[index].setColor(0.3f, 0.6f, 1f, 0.7f + electricIntensity * 0.3f);
    }

    public void update(float delta) {
        if (!active) return;

        animationTime += delta;
        electricIntensity = (float) Math.sin(animationTime * electricPulseSpeed) * 0.5f + 0.5f;

        // Update animation frame
        if (animation != null) {
            Texture currentFrame = animation.getKeyFrame(animationTime, true);
            for (Sprite sprite : sprites) {
                if (sprite != null) {
                    sprite.setTexture(currentFrame);
                }
            }
        }

        // Update sprite colors
        for (int i = 0; i < 4; i++) {
            updateSprite(i);
        }
    }

    public boolean checkCollision(Player player) {
        if (!active || player == null) return false;

        for (Rectangle boundingBox : boundingBoxes) {
            if (boundingBox.overlaps(player.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    public void draw() {
        if (!active) return;

        for (Sprite sprite : sprites) {
            if (sprite != null) {
                // Draw main barrier
                sprite.draw(Main.getBatch());

                // Draw brighter edges
                drawElectricEdges(sprite);
            }
        }
    }

    private void drawElectricEdges(Sprite sprite) {
        if (sprite == null) return;

        // Save original sprite properties
        Texture originalTexture = sprite.getTexture();
        float originalWidth = sprite.getWidth();
        float originalHeight = sprite.getHeight();
        float originalX = sprite.getX();
        float originalY = sprite.getY();
        Color originalColor = sprite.getColor();

        // Draw brighter electric edges
        float edgeWidth = 3f + electricIntensity * 2f;
        float edgeAlpha = 0.8f + electricIntensity * 0.2f;
        Color edgeColor = new Color(0.8f, 0.9f, 1f, edgeAlpha);

        // Draw edges based on barrier orientation
        if (originalWidth > originalHeight) {
            // Horizontal barrier (top or bottom)
            // Top edge
            sprite.setSize(originalWidth, edgeWidth);
            sprite.setPosition(originalX, originalY + originalHeight - edgeWidth);
            sprite.setColor(edgeColor);
            sprite.draw(Main.getBatch());

            // Bottom edge
            sprite.setPosition(originalX, originalY);
            sprite.draw(Main.getBatch());
        } else {
            // Vertical barrier (left or right)
            // Left edge
            sprite.setSize(edgeWidth, originalHeight);
            sprite.setPosition(originalX, originalY);
            sprite.setColor(edgeColor);
            sprite.draw(Main.getBatch());

            // Right edge
            sprite.setPosition(originalX + originalWidth - edgeWidth, originalY);
            sprite.draw(Main.getBatch());
        }

        // Restore original sprite properties
        sprite.setTexture(originalTexture);
        sprite.setSize(originalWidth, originalHeight);
        sprite.setPosition(originalX, originalY);
        sprite.setColor(originalColor);
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
        if (sprites != null) {
            for (Sprite sprite : sprites) {
                if (sprite != null && sprite.getTexture() != null) {
                    sprite.getTexture().dispose();
                }
            }
        }
    }
}
