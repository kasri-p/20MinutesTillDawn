package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Player;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.Map;

public class PlayerController {
    private final float ABILITY_COOLDOWN = 0.5f;
    private Player player;
    private boolean recentlyFlipped = false;
    private Animation<Texture> currentAnimation;
    private float stateTime = 0;
    private boolean isMoving = false;
    private WeaponController weaponController;
    private Texture mapTexture;
    private float mapWidth;
    private float mapHeight;

    private float lastAbilityActivation = 0f;

    public PlayerController(Player player) {
        this.player = player;

        this.mapTexture = new Texture("Images/map.png");
        this.mapWidth = mapTexture.getWidth();
        this.mapHeight = mapTexture.getHeight();
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;
        lastAbilityActivation += deltaTime;

        player.update(deltaTime);

        player.getPlayerSprite().setPosition(
            player.getPosX() - player.getPlayerSprite().getWidth() / 2,
            player.getPosY() - player.getPlayerSprite().getHeight() / 2
        );

        updateAnimation();
        player.updateLevelUpAnimation(deltaTime);
        player.getPlayerSprite().draw(Main.getBatch());

        if (player.isLevelingUp()) {
            drawLevelUpAnimation();
        }

        handlePlayerInput();
        handleAbilityInput();

        updateAbilities(deltaTime);
    }

    private void updateAbilities(float deltaTime) {
        for (Abilities ability : Abilities.values()) {
            ability.update(deltaTime);
        }

        applyActiveAbilityEffects();
    }

    private void applyActiveAbilityEffects() {
        if (Abilities.SPEEDY.isActive()) {
            if (isMoving) {
                addSpeedEffects();
            }
        }

        if (Abilities.DAMAGER.isActive()) {

            addDamageEffects();
        }

        if (Abilities.SHIELD.isActive()) {
            addShieldEffects();
        }

        if (Abilities.MULTISHOT.isActive()) {
            addMultishotEffects();
        }
    }


    private void addSpeedEffects() {

        if (stateTime % 1.0f < 0.016f) {
            Gdx.app.debug("PlayerController", "Speed boost active - remaining: " +
                String.format("%.1f", Abilities.SPEEDY.getRemainingDuration()));
        }
    }


    private void addDamageEffects() {
        // Could implement weapon glow, player aura, or damage indicators
        // Player sprite could have a red tint or glow effect
    }


    private void addShieldEffects() {
        // Could implement a blue/white shield bubble around player
        // Or make player sprite have a shield glow
    }


    private void addMultishotEffects() {
        // Could add crosshair effects or weapon modifications
    }

    private void drawLevelUpAnimation() {
        Texture levelUpFrame = player.getLevelUpFrame();
        if (levelUpFrame != null) {
            float animationProgress = player.getLevelUpAnimationProgress();

            float scale;
            float animationHeight;
            float animationWidth;

            if (animationProgress < 0.2f) {
                float strikeProgress = animationProgress / 0.2f;
                scale = 0.8f + strikeProgress * 0.4f;
                animationWidth = 32f * scale;
                float screenTop = player.getPosY() + 600f;
                animationHeight = screenTop - player.getPosY();
            } else if (animationProgress < 0.3f) {
                scale = 1.2f + ((animationProgress - 0.2f) / 0.1f) * 0.3f;
                animationWidth = 48f * scale;
                animationHeight = 48f * scale;
            } else {
                scale = 1.5f + ((animationProgress - 0.3f) / 0.7f) * 0.2f;
                animationWidth = 110f * scale;
                animationHeight = 55f * scale;
            }

            float centerX = player.getPosX();
            float centerY = player.getPosY();
            float animationX, animationY;

            if (animationProgress < 0.2f) {
                float screenTop = centerY + 600f;
                animationX = centerX - animationWidth / 2;
                animationY = centerY;
            } else {
                animationX = centerX - animationWidth / 2;
                animationY = centerY - animationHeight / 2;
            }

            float alpha = 1.0f;
            if (animationProgress < 0.25f) {
                alpha = 0.9f + 0.1f * (float) Math.sin(animationProgress * 25f);
            } else {
                alpha = 0.95f + 0.05f * (float) Math.sin(animationProgress * 6f);
            }

            float originalAlpha = Main.getBatch().getColor().a;
            Main.getBatch().setColor(1f, 1f, 1f, alpha);

            Main.getBatch().draw(
                levelUpFrame,
                animationX,
                animationY,
                animationWidth,
                animationHeight
            );

            Main.getBatch().setColor(1f, 1f, 1f, originalAlpha);
        }
    }

    private void updateAnimation() {
        Animation<Texture> animation = null;

        if (isMoving) {
            animation = GameAssetManager.getGameAssetManager().getPlayerRunAnimation();
            player.setPlayerIdle(false);
            player.setPlayerRunning(true);
        } else {
            animation = GameAssetManager.getGameAssetManager().getPlayerIdleAnimation();
            player.setPlayerIdle(true);
            player.setPlayerRunning(false);
        }

        if (animation != currentAnimation) {
            currentAnimation = animation;
        }

        if (currentAnimation != null) {
            Texture currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            player.getPlayerSprite().setTexture(currentFrame);
        }
    }

    public void handlePlayerInput() {
        isMoving = false;

        float newX = player.getPosX();
        float newY = player.getPosY();
        Map<String, String> keyBinds = App.getKeybinds();

        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Up")))) {
            newY += player.getSpeed();
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Right")))) {
            newX += player.getSpeed();
            isMoving = true;
            if (recentlyFlipped) {
                player.getPlayerSprite().flip(true, false);
                recentlyFlipped = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Down")))) {
            newY -= player.getSpeed();
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Move Left")))) {
            newX -= player.getSpeed();
            isMoving = true;
            if (!recentlyFlipped) {
                player.getPlayerSprite().flip(true, false);
                recentlyFlipped = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.valueOf(keyBinds.get("Reload")))) {
            handleReload();
        }

        float halfWidth = player.getPlayerSprite().getWidth() / 2f;
        float halfHeight = player.getPlayerSprite().getHeight() / 2f;

        newX = MathUtils.clamp(newX, halfWidth, mapWidth - halfWidth);
        newY = MathUtils.clamp(newY, halfHeight, mapHeight - halfHeight);

        player.setPosX(newX);
        player.setPosY(newY);
    }

    private void handleAbilityInput() {
        if (lastAbilityActivation < ABILITY_COOLDOWN) {
            return;
        }

        // Handle ability activation keys (1-4 for quick activation)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            activateAbility(Abilities.DAMAGER);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            activateAbility(Abilities.SPEEDY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            activateAbility(Abilities.SHIELD);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            activateAbility(Abilities.MULTISHOT);
        }
    }


    private void activateAbility(Abilities ability) {
        if (ability.canActivate()) {
            ability.activate();

            switch (ability) {
                case DAMAGER:
                    player.activateDamager();
                    Gdx.app.log("PlayerController", "Damager ability activated!");
                    break;
                case SPEEDY:
                    player.activateSpeedy();
                    Gdx.app.log("PlayerController", "Speedy ability activated!");
                    break;
                case SHIELD:
                    player.activateShield();
                    Gdx.app.log("PlayerController", "Shield ability activated!");
                    break;
                case MULTISHOT:
                    player.activateMultishot();
                    Gdx.app.log("PlayerController", "Multishot ability activated!");
                    break;
                default:
                    Gdx.app.log("PlayerController", "Attempted to activate passive ability: " + ability.getName());
                    break;
            }

            lastAbilityActivation = 0f;

            if (App.isSFX()) {
                Main.getMain().getClickSound().play();
            }
        } else {
            if (ability.isActive()) {
                Gdx.app.log("PlayerController", ability.getName() + " is already active!");
            } else if (ability.getRemainingCooldown() > 0) {
                Gdx.app.log("PlayerController", ability.getName() + " is on cooldown: " +
                    String.format("%.1f", ability.getRemainingCooldown()) + "s remaining");
            }
        }
    }

    public String getAbilityStatusText() {
        StringBuilder status = new StringBuilder();
        status.append("Abilities:\n");

        for (Abilities ability : Abilities.values()) {
            if (ability.getType() == Abilities.AbilityType.ACTIVE) {
                status.append(ability.getName()).append(": ");
                if (ability.isActive()) {
                    status.append("Active (").append(String.format("%.1f", ability.getRemainingDuration())).append("s)");
                } else if (ability.getRemainingCooldown() > 0) {
                    status.append("Cooldown (").append(String.format("%.1f", ability.getRemainingCooldown())).append("s)");
                } else {
                    status.append("Ready");
                }
                status.append("\n");
            }
        }

        return status.toString();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void handleReload() {
        weaponController.startReload();
    }

    public void setWeaponController(WeaponController weaponController) {
        this.weaponController = weaponController;
    }
}
