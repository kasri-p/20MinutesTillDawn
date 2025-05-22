package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.utils.GameAssetManager;

public class Player {
    private Texture playerTexture = new Texture(Gdx.files.internal("Images/characters/Shana/run0.png"));
    private Sprite playerSprite = new Sprite(playerTexture);
    private float posX = 0;
    private float posY = 0;
    private int playerHealth;
    private Rectangle boundingBox;
    private float time = 0;
    private float speed = 5;
    private boolean isPlayerIdle = true;
    private boolean isPlayerRunning = false;
    private Characters character;
    private int level = 1;

    private int XP;

    // Level up animation properties
    private boolean isLevelingUp = false;
    private float levelUpAnimationTime = 0f;
    private Animation<Texture> levelUpAnimation;

    public Player(Characters character) {
        playerSprite.setSize(playerTexture.getWidth() * 2, playerTexture.getHeight() * 2);
        playerSprite.setOriginCenter();
        boundingBox = new Rectangle(0, 0, playerTexture.getWidth() * 3, playerTexture.getHeight() * 3);
        this.character = character;
        this.speed = character.getSpeed();
        this.playerHealth = character.getHp();

        // Get the level up animation from GameAssetManager
        this.levelUpAnimation = GameAssetManager.getGameAssetManager().getLevelUpAnimation();
    }

    public float getSpeed() {
        return speed;
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    public void setPlayerTexture(Texture playerTexture) {
        this.playerTexture = playerTexture;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
        updateBoundingBox();
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
        updateBoundingBox();
    }

    private void updateBoundingBox() {
        boundingBox.setPosition(
            posX - playerSprite.getWidth() / 2,
            posY - playerSprite.getHeight() / 2
        );
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public boolean isPlayerIdle() {
        return isPlayerIdle;
    }

    public void setPlayerIdle(boolean playerIdle) {
        isPlayerIdle = playerIdle;
    }

    public boolean isPlayerRunning() {
        return isPlayerRunning;
    }

    public void setPlayerRunning(boolean playerRunning) {
        isPlayerRunning = playerRunning;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public Characters getCharacter() {
        return character;
    }

    public void dispose() {
        playerTexture.dispose();
    }

    public void addXP(int xp) {
        this.XP += xp;
        updateLevel();
    }

    private void updateLevel() {
        int tempXP = XP;
        int newLevel = 1;
        int xpNeeded = 20;

        while (tempXP >= xpNeeded) {
            tempXP -= xpNeeded;
            newLevel++;
            xpNeeded = 20 * newLevel;
        }

        // If level has increased
        if (newLevel > level) {
            levelUp(newLevel);
        }

        this.level = newLevel;
    }

    private void levelUp(int newLevel) {
        // Start the level up animation
        isLevelingUp = true;
        levelUpAnimationTime = 0f;

        // You can add other level up effects here like:
        // - Playing a sound effect
        // - Increasing player stats
        // - Showing a level up message

        Gdx.app.log("Player", "Level up! New level: " + newLevel);
    }

    // Method to update the level up animation
    public void updateLevelUpAnimation(float delta) {
        if (isLevelingUp && levelUpAnimation != null) {
            levelUpAnimationTime += delta;

            // Check if animation is complete
            if (levelUpAnimationTime >= levelUpAnimation.getAnimationDuration()) {
                isLevelingUp = false;
                levelUpAnimationTime = 0f;
            }
        }
    }

    // Method to get the current level up animation frame
    public Texture getLevelUpFrame() {
        if (isLevelingUp && levelUpAnimation != null) {
            return levelUpAnimation.getKeyFrame(levelUpAnimationTime, false);
        }
        return null;
    }

    // Getters for animation state
    public boolean isLevelingUp() {
        return isLevelingUp;
    }

    public float getLevelUpAnimationProgress() {
        if (isLevelingUp && levelUpAnimation != null) {
            return levelUpAnimationTime / levelUpAnimation.getAnimationDuration();
        }
        return 0f;
    }

    public int getXP() {
        return XP;
    }

    public int getLevel() {
        return level;
    }
}

