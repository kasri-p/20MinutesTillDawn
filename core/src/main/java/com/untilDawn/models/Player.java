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

    private boolean isLevelingUp = false;
    private float levelUpAnimationTime = 0f;
    private Animation<Texture> levelUpAnimation;

    private boolean isInvincible = false;
    private float invincibilityTimer = 0f;
    private float invincibilityDuration = 1f;
    private float flashTimer = 0f;
    private boolean isFlashing = false;

    private boolean shouldShowLevelUpWindow = false;
    private boolean levelUpWindowShown = false;

    public Player(Characters character) {
        playerSprite.setSize(playerTexture.getWidth() * 2, playerTexture.getHeight() * 2);
        playerSprite.setOriginCenter();
        boundingBox = new Rectangle(0, 0, playerTexture.getWidth() * 3, playerTexture.getHeight() * 3);
        this.character = character;
        this.speed = character.getSpeed();
        this.playerHealth = character.getHp();

        this.levelUpAnimation = GameAssetManager.getGameAssetManager().getLevelUpAnimation();
    }

    public void update(float delta) {
        updateInvincibility(delta);

        updateLevelUpAnimation(delta);

        updateBoundingBox();
    }

    private void updateInvincibility(float delta) {
        if (isInvincible) {
            invincibilityTimer += delta;
            flashTimer += delta;

            if (flashTimer >= 0.1f) {
                isFlashing = !isFlashing;
                flashTimer = 0f;

                if (isFlashing) {
                    playerSprite.setColor(1f, 1f, 1f, 0.5f);
                } else {
                    playerSprite.setColor(1f, 1f, 1f, 1f);
                }
            }

            if (invincibilityTimer >= invincibilityDuration) {
                isInvincible = false;
                invincibilityTimer = 0f;
                flashTimer = 0f;
                isFlashing = false;
                playerSprite.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    public void setInvincible(boolean invincible, float duration) {
        this.isInvincible = invincible;
        this.invincibilityDuration = duration;
        this.invincibilityTimer = 0f;
        this.flashTimer = 0f;

        if (!invincible) {
            isFlashing = false;
            playerSprite.setColor(1f, 1f, 1f, 1f);
        }
    }

    public void takeDamage(int damage) {
        if (!isInvincible) {
            playerHealth -= 1;
        }
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

    public boolean isInvincible() {
        return isInvincible;
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
        isLevelingUp = true;
        levelUpAnimationTime = 0f;

        shouldShowLevelUpWindow = true;
        levelUpWindowShown = false;

        Gdx.app.log("Player", "Level up! New level: " + newLevel);
    }

    public void updateLevelUpAnimation(float delta) {
        if (isLevelingUp && levelUpAnimation != null) {
            levelUpAnimationTime += delta;

            if (levelUpAnimationTime >= levelUpAnimation.getAnimationDuration()) {
                isLevelingUp = false;
                levelUpAnimationTime = 0f;
            }
        }
    }

    public Texture getLevelUpFrame() {
        if (isLevelingUp && levelUpAnimation != null) {
            return levelUpAnimation.getKeyFrame(levelUpAnimationTime, false);
        }
        return null;
    }

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

    public boolean shouldShowLevelUpWindow() {
        return shouldShowLevelUpWindow && !levelUpWindowShown;
    }

    public void setLevelUpWindowShown() {
        this.levelUpWindowShown = true;
        this.shouldShowLevelUpWindow = false;
    }

    public void resetLevelUpWindow() {
        this.shouldShowLevelUpWindow = false;
        this.levelUpWindowShown = false;
    }
}
