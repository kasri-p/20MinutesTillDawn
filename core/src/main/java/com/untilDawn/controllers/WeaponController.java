package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Bullet;
import com.untilDawn.models.Player;
import com.untilDawn.models.Weapon;
import com.untilDawn.models.enums.Abilities;
import com.untilDawn.models.enums.Weapons;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.Iterator;

public class WeaponController {
    private final float MUZZLE_FLASH_DURATION = 0.05f;
    private Weapon weapon;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private float screenCenterX;
    private float screenCenterY;
    private PlayerController playerController;
    private boolean isReloading = false;
    private float reloadTimer = 0;
    private float reloadDuration = 1.0f;

    // Camera for proper coordinate conversion
    private OrthographicCamera camera;
    private Vector3 worldCoords = new Vector3();

    // Reload bar progress
    private Texture reloadBarBg;
    private Texture reloadBarFill;
    private float reloadBarWidth = 80f;
    private float reloadBarHeight = 8f;
    private float reloadBarOffsetY = 37f;
    private Animation<Texture> reloadAnimation;
    private float reloadAnimationTime = 0;
    private boolean usingReloadAnimation = false;
    private Texture stillTexture;

    // Muzzle flash properties
    private Texture muzzleFlashTexture;
    private boolean showMuzzleFlash = false;
    private float muzzleFlashTimer = 0;
    private float muzzleFlashOffsetX = 0;
    private float muzzleFlashOffsetY = 0;
    private float muzzleFlashScale = 1.0f;

    public WeaponController(Weapon weapon) {
        this.weapon = weapon;
        updateScreenCenter();

        if (weapon.getWeapon() != null) {
            this.reloadDuration = weapon.getWeapon().getReloadTime();
            loadStillTexture();
        }

        muzzleFlashTexture = GameAssetManager.getGameAssetManager().getMuzzleFlash();
        updateMuzzleFlashProperties();
    }

    private void loadStillTexture() {
        if (weapon.getWeapon() != null) {
            String weaponName = weapon.getWeapon().getName().replaceAll("\\s+", "").toLowerCase();
            String stillPath = "Images/weapons/" + weaponName + "/still.png";
            if (Gdx.files.internal(stillPath).exists()) {
                stillTexture = new Texture(Gdx.files.internal(stillPath));
                weapon.getSprite().setTexture(stillTexture);
                weapon.getSprite().setScale(0.7f);
            }
        }
    }

    private void updateMuzzleFlashProperties() {
        if (weapon.getWeapon() == null) {
            muzzleFlashOffsetX = 15f;
            muzzleFlashScale = 1.5f;
            return;
        }

        switch (weapon.getWeapon()) {
            case Shotgun:
                muzzleFlashOffsetX = 22f;
                muzzleFlashScale = 4f;
                break;
            case Dual_Smg:
                muzzleFlashOffsetX = 14f;
                muzzleFlashScale = 0.4f;
                break;
            case Revolver:
                muzzleFlashOffsetX = 18f;
                muzzleFlashScale = 0.6f;
                break;
            default:
                muzzleFlashOffsetX = 15f;
                muzzleFlashScale = 0.5f;
                break;
        }
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    private Vector2 screenToWorldCoordinates(int screenX, int screenY) {
        if (camera != null) {
            worldCoords.set(screenX, screenY, 0);
            camera.unproject(worldCoords);
            return new Vector2(worldCoords.x, worldCoords.y);
        } else {
            float playerX = playerController != null ? playerController.getPlayer().getPosX() : 0;
            float playerY = playerController != null ? playerController.getPlayer().getPosY() : 0;

            float worldX = screenX - (float) Gdx.graphics.getWidth() / 2 + playerX;
            float worldY = Gdx.graphics.getHeight() - screenY - (float) Gdx.graphics.getHeight() / 2 + playerY;
            return new Vector2(worldX, worldY);
        }
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        updateScreenCenter();

        if (playerController != null) {
            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();

            Vector2 worldMouse = screenToWorldCoordinates(mouseX, mouseY);
            float worldMouseX = worldMouse.x;
            float worldMouseY = worldMouse.y;

            float dirX = worldMouseX - playerX;
            float dirY = worldMouseY - playerY;

            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length != 0) {
                dirX /= length;
                dirY /= length;
            }

            float angle = (float) Math.toDegrees(Math.atan2(dirY, dirX));

            float weaponOffsetDistance = 19.0f;
            float weaponPosX = playerX + dirX * weaponOffsetDistance;
            float weaponPosY = playerY + dirY * weaponOffsetDistance;

            weapon.getSprite().setPosition(
                weaponPosX - weapon.getSprite().getWidth() / 2,
                weaponPosY - weapon.getSprite().getHeight() / 2
            );

            if (weapon.getWeapon() == Weapons.Shotgun) {
                weapon.getSprite().setScale(0.8f);
            }

            weapon.getSprite().setRotation(angle);
            weapon.getSprite().setFlip(false, angle > 90 || angle < -90);

            if (showMuzzleFlash) {
                updateMuzzleFlashTimer(deltaTime);
                if (showMuzzleFlash) {
                    drawMuzzleFlash(playerX, playerY, dirX, dirY, angle);
                }
            }
        }

        updateReloading(deltaTime);
        weapon.getSprite().draw(Main.getBatch());

        if (isReloading) {
            drawReloadBar();
        }

        updateBullets(deltaTime);
    }

    public void handleWeaponRotation(int x, int y) {
        Sprite weaponSprite = weapon.getSprite();

        float playerX = 0;
        float playerY = 0;

        if (playerController != null) {
            playerX = playerController.getPlayer().getPosX();
            playerY = playerController.getPlayer().getPosY();
        }

        Vector2 worldMouse = screenToWorldCoordinates(x, y);
        float worldMouseX = worldMouse.x;
        float worldMouseY = worldMouse.y;

        float dirX = worldMouseX - playerX;
        float dirY = worldMouseY - playerY;

        float angle = (float) Math.toDegrees(Math.atan2(dirY, dirX));

        boolean shouldFlip = (angle > 90 && angle < 270) || (angle < -90 && angle > -270);

        if (weaponSprite.isFlipY() != shouldFlip) {
            weaponSprite.setFlip(false, shouldFlip);
        }

        weaponSprite.setRotation(angle);
    }

    public void handleWeaponShoot(int x, int y) {
        if (isReloading || weapon.getAmmo() <= 0) {
            if (weapon.getAmmo() <= 0) {
                checkAutoReload();
            }
            return;
        }

        GameAssetManager.getGameAssetManager().playShot();

        showMuzzleFlash = true;
        muzzleFlashTimer = 0;

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        // Get base weapon stats
        int baseProjectileCount = 1;
        float bulletSpeed = 10.0f;
        int baseBulletDamage = weapon.getWeapon().getDamage();

        if (weapon.getWeapon() != null) {
            baseProjectileCount = weapon.getWeapon().getProjectileCount();

            if (weapon.getWeapon() == Weapons.Shotgun) {
                bulletSpeed = 8.0f;
            } else if (weapon.getWeapon() == Weapons.Dual_Smg) {
                bulletSpeed = 15.0f;
            } else if (weapon.getWeapon() == Weapons.Revolver) {
                bulletSpeed = 12.0f;
            }
        }

        // Apply ability modifiers
        Player player = playerController.getPlayer();

        // Calculate final projectile count with PROCREASE and MULTISHOT bonuses
        int finalProjectileCount = baseProjectileCount;
        finalProjectileCount += player.getProjectileBonus(); // PROCREASE bonus

        // MULTISHOT ability adds additional projectiles when active
        if (Abilities.MULTISHOT.isActive()) {
            finalProjectileCount += 2; // Add 2 more projectiles for multishot
        }

        int finalBulletDamage = baseBulletDamage;
        if (Abilities.DAMAGER.isActive()) {
            finalBulletDamage += player.getDamageBonus();
            // Add visual feedback for damage boost
            showEnhancedMuzzleFlash();
        }

        Gdx.app.log("WeaponController", String.format("Shooting: %d projectiles, %d damage each",
            finalProjectileCount, finalBulletDamage));

        for (int i = 0; i < finalProjectileCount; i++) {
            Bullet newBullet = new Bullet((int) playerX, (int) playerY);
            newBullet.setDamage(finalBulletDamage);
            System.out.println(newBullet.getDamage());

            Vector2 direction = new Vector2(x - playerX, y - playerY).nor();

            if (finalProjectileCount > 1) {
                float spreadAngle = calculateSpreadAngle(finalProjectileCount);
                float angle = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));

                float bulletAngle = angle + (i - (finalProjectileCount - 1) / 2f) * (spreadAngle / (finalProjectileCount - 1));

                float radians = (float) Math.toRadians(bulletAngle);
                direction = new Vector2((float) Math.cos(radians), (float) Math.sin(radians));

                float speedVariation = MathUtils.random(-0.5f, 0.5f);
                newBullet.setSpeed(bulletSpeed + speedVariation);
            } else {
                newBullet.setSpeed(bulletSpeed);
            }

            newBullet.setDirection(direction);

            newBullet.getSprite().setPosition(
                playerX - newBullet.getSprite().getWidth() / 2,
                playerY - newBullet.getSprite().getHeight() / 2
            );

            bullets.add(newBullet);
        }

        weapon.setAmmo(weapon.getAmmo() - 1);
    }


    private float calculateSpreadAngle(int projectileCount) {
        if (projectileCount <= 1) return 0f;

        float baseSpread = 15f;

        if (projectileCount <= 3) {
            return baseSpread;
        } else if (projectileCount <= 5) {
            return baseSpread * 1.5f;
        } else {
            return baseSpread * 2f;
        }
    }

    private void showEnhancedMuzzleFlash() {
        muzzleFlashScale *= 1.5f;
    }

    private void updateMuzzleFlashTimer(float deltaTime) {
        if (showMuzzleFlash) {
            muzzleFlashTimer += deltaTime;
            if (muzzleFlashTimer >= MUZZLE_FLASH_DURATION) {
                showMuzzleFlash = false;
                muzzleFlashTimer = 0;

                updateMuzzleFlashProperties();
            }
        }
    }

    private void drawMuzzleFlash(float playerX, float playerY, float dirX, float dirY, float angle) {
        if (muzzleFlashTexture == null) return;

        float weaponCenterX = weapon.getSprite().getX() + weapon.getSprite().getWidth() / 2;
        float weaponCenterY = weapon.getSprite().getY() + weapon.getSprite().getHeight() / 2;

        float radians = (float) Math.toRadians(angle);
        float flashX = weaponCenterX + (float) (Math.cos(radians) * muzzleFlashOffsetX) - muzzleFlashTexture.getWidth() * muzzleFlashScale / 2;
        float flashY = weaponCenterY + (float) (Math.sin(radians) * muzzleFlashOffsetX) - muzzleFlashTexture.getHeight() * muzzleFlashScale / 2;

        // Apply enhanced color if damage boost is active
        if (Abilities.DAMAGER.isActive()) {
            Main.getBatch().setColor(1.2f, 0.8f, 0.6f, 1.0f); // Orange-red tint for damage boost
        }

        Main.getBatch().draw(
            muzzleFlashTexture,
            flashX,
            flashY,
            muzzleFlashTexture.getWidth() * muzzleFlashScale / 2,
            muzzleFlashTexture.getHeight() * muzzleFlashScale / 2,
            muzzleFlashTexture.getWidth() * muzzleFlashScale,
            muzzleFlashTexture.getHeight() * muzzleFlashScale,
            1.0f, 1.0f,
            angle,
            0, 0,
            muzzleFlashTexture.getWidth(), muzzleFlashTexture.getHeight(),
            false, weapon.getSprite().isFlipY()
        );

        // Reset color
        Main.getBatch().setColor(1f, 1f, 1f, 1f);
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    private void updateReloading(float deltaTime) {
        if (isReloading) {
            reloadTimer += deltaTime;

            if (usingReloadAnimation) {
                reloadAnimationTime += deltaTime;

                if (reloadAnimation != null) {
                    Texture currentFrame = reloadAnimation.getKeyFrame(reloadAnimationTime, false);
                    weapon.getSprite().setTexture(currentFrame);
                }
            }

            if (reloadTimer >= reloadDuration) {
                completeReload();
            }
        }
    }

    private void completeReload() {
        isReloading = false;
        reloadTimer = 0;
        usingReloadAnimation = false;

        if (stillTexture != null) {
            weapon.getSprite().setTexture(stillTexture);
        }

        // Apply ammo capacity bonuses from abilities
        int baseAmmo = weapon.getWeapon() != null ? weapon.getWeapon().getAmmoMax() : 30;
        int finalAmmo = baseAmmo;

        if (playerController != null && playerController.getPlayer() != null) {
            finalAmmo += playerController.getPlayer().getAmmoBonus(); // AMOCREASE bonus
        }

        weapon.setAmmo(finalAmmo);

        Gdx.app.log("WeaponController", "Reload complete: " + finalAmmo + " rounds loaded");
    }

    private void drawReloadBar() {
        if (playerController == null) return;

        if (reloadBarBg == null) {
            reloadBarBg = GameAssetManager.getGameAssetManager().getReloadBarBg();
        }
        if (reloadBarFill == null) {
            reloadBarFill = GameAssetManager.getGameAssetManager().getReloadBarFill();
        }

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float barX = playerX - reloadBarWidth / 2;
        float barY = playerY + reloadBarOffsetY;

        Main.getBatch().draw(reloadBarBg, barX, barY, reloadBarWidth, reloadBarHeight);

        float progress = reloadTimer / reloadDuration;
        float indicatorWidth = reloadBarFill.getWidth();
        float indicatorHeight = reloadBarHeight;

        float indicatorX = barX + (reloadBarWidth - indicatorWidth) * progress;

        Main.getBatch().draw(reloadBarFill, indicatorX, barY, indicatorWidth, indicatorHeight);
    }

    public void startReload() {
        if (!isReloading && weapon.getAmmo() < getMaxAmmoWithBonuses()) {
            isReloading = true;
            reloadTimer = 0;
            reloadAnimationTime = 0;

            if (weapon.getWeapon() != null) {
                try {
                    reloadAnimation = GameAssetManager.getGameAssetManager().getWeaponReloadAnimation(weapon.getWeapon());

                    if (reloadAnimation != null) {
                        usingReloadAnimation = true;

                        Texture firstFrame = reloadAnimation.getKeyFrame(0);
                        if (firstFrame != null) {
                            weapon.getSprite().setTexture(firstFrame);
                        }
                    } else {
                        usingReloadAnimation = false;
                        if (stillTexture != null) {
                            weapon.getSprite().setTexture(stillTexture);
                        }
                    }
                } catch (Exception e) {
                    Gdx.app.error("WeaponController", "Error loading reload animation: " + e.getMessage());
                    usingReloadAnimation = false;
                }
            }

            GameAssetManager.getGameAssetManager().playReloadSound();
        }
    }

    /**
     * Get maximum ammo capacity including ability bonuses
     */
    private int getMaxAmmoWithBonuses() {
        int baseAmmo = weapon.getWeapon() != null ? weapon.getWeapon().getAmmoMax() : 30;

        if (playerController != null && playerController.getPlayer() != null) {
            return baseAmmo + playerController.getPlayer().getAmmoBonus();
        }

        return baseAmmo;
    }

    public void cancelReload() {
        if (isReloading) {
            isReloading = false;
            reloadTimer = 0;
            usingReloadAnimation = false;

            if (stillTexture != null) {
                weapon.getSprite().setTexture(stillTexture);
            }
        }
    }

    public void updateBullets(float deltaTime) {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            if (!bullet.isActive()) {
                iterator.remove();
                continue;
            }

            bullet.update(deltaTime);

            // Apply visual effects for enhanced bullets
            if (Abilities.DAMAGER.isActive()) {
                // Draw enhanced bullet with damage boost visual
                Main.getBatch().setColor(1.2f, 0.8f, 0.6f, 1.0f);
                bullet.getSprite().draw(Main.getBatch());
                Main.getBatch().setColor(1f, 1f, 1f, 1f);
            } else {
                bullet.getSprite().draw(Main.getBatch());
            }

            float playerX = playerController != null ? playerController.getPlayer().getPosX() : 0;
            float playerY = playerController != null ? playerController.getPlayer().getPosY() : 0;

            if (isBulletTooFar(bullet, playerX, playerY)) {
                bullet.setActive(false);
                iterator.remove();
            }
        }
    }

    public void checkAutoReload() {
        if (weapon.getAmmo() <= 0 && App.isAutoReloadEnabled() && !isReloading) {
            startReload();
        }
    }

    private boolean isBulletTooFar(Bullet bullet, float playerX, float playerY) {
        float bulletX = bullet.getSprite().getX() + bullet.getSprite().getWidth() / 2;
        float bulletY = bullet.getSprite().getY() + bullet.getSprite().getHeight() / 2;

        float distanceSquared = (bulletX - playerX) * (bulletX - playerX) +
            (bulletY - playerY) * (bulletY - playerY);

        float maxDistanceSquared = 1200 * 1200;

        return distanceSquared > maxDistanceSquared;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;

        if (weapon.getWeapon() != null) {
            this.reloadDuration = weapon.getWeapon().getReloadTime();
        }

        loadStillTexture();
        updateMuzzleFlashProperties();
    }

    public void handleResize(int width, int height) {
        updateScreenCenter();
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadProgress() {
        if (!isReloading) return 0;
        return reloadTimer / reloadDuration;
    }

    public void dispose() {
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();

        if (reloadBarBg != null) {
            reloadBarBg.dispose();
            reloadBarBg = null;
        }
        if (reloadBarFill != null) {
            reloadBarFill.dispose();
            reloadBarFill = null;
        }
        if (stillTexture != null) {
            stillTexture.dispose();
            stillTexture = null;
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
}
