package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
        getSprite().setScale(0.75f);

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
            for (int i = 0; i <= 4; i++) {
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

    public ElectricBarrier getBarrier() {
        return perimeterBarrier;
    }

    // Boss states
    private enum ElderState {
        WALKING,
        CHARGING,
        ATTACKING
    }

    public static class ElectricBarrier {
        private float outerWidth, outerHeight;
        private float currentWidth, currentHeight;
        private float thickness = 30f;
        private boolean active = true;
        private Rectangle boundingBox;
        private float animationTime = 0f;
        private float electricPulseSpeed = 4f;  // pulse speed
        private float electricIntensity = 0f;
        private float shrinkSpeed = 20f;
        private float minSize = 200f;
        private boolean isShrinking = true;


        public ElectricBarrier(float mapWidth, float mapHeight) {
            this.outerWidth = mapWidth;
            this.outerHeight = mapHeight;
            this.currentWidth = mapWidth;
            this.currentHeight = mapHeight;

            boundingBox = new Rectangle(
                (outerWidth - currentWidth) / 2,
                (outerHeight - currentHeight) / 2,
                currentWidth,
                currentHeight
            );
        }

        public void update(float delta) {
            if (!active) return;

            animationTime += delta;
            electricIntensity = (float) Math.sin(animationTime * electricPulseSpeed) * 0.5f + 0.5f;

            if (isShrinking) {
                float newWidth = currentWidth - shrinkSpeed * delta * 2;
                float newHeight = currentHeight - shrinkSpeed * delta * 2;

                if (newWidth <= minSize || newHeight <= minSize) {
                    newWidth = Math.max(minSize, newWidth);
                    newHeight = Math.max(minSize, newHeight);
                    isShrinking = false;
                }

                currentWidth = newWidth;
                currentHeight = newHeight;

                boundingBox.set(
                    (outerWidth - currentWidth) / 2,
                    (outerHeight - currentHeight) / 2,
                    currentWidth,
                    currentHeight
                );
            }
        }

        public boolean checkCollision(Player player) {
            if (!active || player == null) return false;
            Rectangle playerBox = player.getBoundingBox();
            return !boundingBox.contains(playerBox);
        }

        public Rectangle getBoundingBox() {
            return boundingBox;
        }

        public float getCurrentWidth() {
            return currentWidth;
        }

        public float getCurrentHeight() {
            return currentHeight;
        }

        public float getOuterWidth() {
            return outerWidth;
        }

        public float getOuterHeight() {
            return outerHeight;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void dispose() {
        }
    }
}

