package com.untilDawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.enums.Characters;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int kills;
    private Texture playerTexture = new Texture(Gdx.files.internal("Images/characters/Shana/run0.png"));
    private Sprite playerSprite = new Sprite(playerTexture);
    private float posX = 0;
    private float posY = 0;
    private int playerHealth;
    private int maxHealth;
    private Rectangle boundingBox;
    private float time = 0;
    private float speed = 5;
    private float baseSpeed = 5;
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

    // Curse animation fields
    private boolean isCurseAnimationPlaying = false;
    private float curseAnimationTime = 0f;
    private Animation<Texture> curseAnimation;
    private float curseAnimationDuration = 0.6f; // Duration for curse animation

    // Ability-related fields
    private List<Abilities> activeAbilities = new ArrayList<>();
    private float regenerationTimer = 0f;
    private int damageBonus = 0;
    private int projectileBonus = 0;
    private int ammoBonus = 0;
    private boolean hasRegeneration = false;
    private boolean hasMultishot = false;

    public Player(Characters character) {
        playerSprite.setSize(playerTexture.getWidth() * 2, playerTexture.getHeight() * 2);
        playerSprite.setOriginCenter();
        boundingBox = new Rectangle(0, 0, playerTexture.getWidth() * 3, playerTexture.getHeight() * 3);
        this.character = character;
        this.baseSpeed = character.getSpeed();
        this.speed = baseSpeed;
        this.maxHealth = character.getHp();
        this.playerHealth = maxHealth;

        this.levelUpAnimation = GameAssetManager.getGameAssetManager().getLevelUpAnimation();
        this.curseAnimation = GameAssetManager.getGameAssetManager().getCurseAnimation();
        this.kills = 0;
    }

    public void update(float delta) {
        updateInvincibility(delta);
        updateLevelUpAnimation(delta);
        updateCurseAnimation(delta);
        updateBoundingBox();
        updateAbilities(delta);
        updateRegeneration(delta);
    }

    private void updateAbilities(float delta) {
        // Update all abilities
        for (Abilities ability : Abilities.values()) {
            ability.update(delta);
        }

        // Apply speed boost if Speedy is active
        if (Abilities.SPEEDY.isActive()) {
            speed = baseSpeed * 2f;
        } else {
            speed = baseSpeed;
        }

        // Apply damage boost if Damager is active
        if (Abilities.DAMAGER.isActive()) {
            damageBonus = (int) (25f * 0.01f * 100); // 25% damage boost
        } else {
            damageBonus = 0;
        }

        // Check multishot
        hasMultishot = Abilities.MULTISHOT.isActive();
    }

    private void updateRegeneration(float delta) {
        if (hasRegeneration && playerHealth < maxHealth) {
            regenerationTimer += delta;
            // Heal every 2 seconds
            float REGENERATION_INTERVAL = 2f;
            if (regenerationTimer >= REGENERATION_INTERVAL) {
                playerHealth = Math.min(playerHealth + 1, maxHealth);
                regenerationTimer = 0f;
            }
        }
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

    private void updateCurseAnimation(float delta) {
        if (isCurseAnimationPlaying) {
            curseAnimationTime += delta;

            if (curseAnimationTime >= curseAnimationDuration) {
                isCurseAnimationPlaying = false;
                curseAnimationTime = 0f;
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
            playerHealth -= damage;
            if (playerHealth < 0) {
                playerHealth = 0;
            }
        }
    }

    public void startCurseAnimation() {
        isCurseAnimationPlaying = true;
        curseAnimationTime = 0f;
        Gdx.app.log("Player", "Curse animation started!");
    }

    public void applyVitality() {
        maxHealth += 1;
        playerHealth = Math.min(playerHealth + 1, maxHealth);
    }

    public void applyProcrease() {
        projectileBonus += 1;
    }

    public void applyAmocrease() {
        ammoBonus += 5;
    }

    public void enableRegeneration() {
        hasRegeneration = true;
        regenerationTimer = 0f;
    }

    public void activateShield() {
        Abilities.SHIELD.activate();
        setInvincible(true, Abilities.SHIELD.getDuration());
    }

    public void activateDamager() {
        Abilities.DAMAGER.activate();
    }

    public void activateSpeedy() {
        Abilities.SPEEDY.activate();
    }

    public void activateMultishot() {
        Abilities.MULTISHOT.activate();
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getProjectileBonus() {
        return projectileBonus;
    }

    public int getAmmoBonus() {
        return ammoBonus;
    }

    public boolean hasMultishot() {
        return hasMultishot;
    }

    public boolean hasRegeneration() {
        return hasRegeneration;
    }

    public float getSpeed() {
        return speed;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
        if (!Abilities.SPEEDY.isActive()) {
            this.speed = baseSpeed;
        }
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
        this.playerHealth = Math.min(playerHealth, maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (playerHealth > maxHealth) {
            playerHealth = maxHealth;
        }
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

    public boolean isCurseAnimationPlaying() {
        return isCurseAnimationPlaying;
    }

    public Texture getCurseAnimationFrame() {
        if (isCurseAnimationPlaying && curseAnimation != null) {
            return curseAnimation.getKeyFrame(curseAnimationTime, false);
        }
        return null;
    }

    public float getCurseAnimationProgress() {
        if (isCurseAnimationPlaying && curseAnimationDuration > 0) {
            return curseAnimationTime / curseAnimationDuration;
        }
        return 0f;
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

        if (newLevel > level) {
            levelUp(newLevel);
        }

        this.level = newLevel;
    }

    private void levelUp(int newLevel) {
        GameAssetManager.getGameAssetManager().playLevelUp();
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

    public void addKill() {
        this.kills += 1;
    }

    public int getKills() {
        return kills;
    }
}
