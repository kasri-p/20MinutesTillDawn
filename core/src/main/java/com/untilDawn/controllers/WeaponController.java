package com.untilDawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.untilDawn.Main;
import com.untilDawn.models.App;
import com.untilDawn.models.Bullet;
import com.untilDawn.models.Weapon;
import com.untilDawn.models.enums.Weapons;
import com.untilDawn.models.utils.GameAssetManager;

import java.util.ArrayList;
import java.util.Iterator;

public class WeaponController {
    private Weapon weapon;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private float screenCenterX;
    private float screenCenterY;
    private PlayerController playerController;

    private boolean isReloading = false;
    private float reloadTimer = 0;
    private float reloadDuration = 1.0f;

    // Reload bar ahh progress
    private Texture reloadBarBg;
    private Texture reloadBarFill;
    private float reloadBarWidth = 50f;
    private float reloadBarHeight = 8f;
    private float reloadBarOffsetY = 40f;// Distance above player head

    private Animation<Texture> reloadAnimation;
    private float reloadAnimationTime = 0;

    public WeaponController(Weapon weapon) {
        this.weapon = weapon;
        updateScreenCenter();

        if (weapon.getWeapon() != null) {
            this.reloadDuration = weapon.getWeapon().getReloadTime();
        }
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        updateScreenCenter();

        if (playerController != null) {
            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            weapon.getSprite().setPosition(
                playerX - weapon.getSprite().getWidth() / 2,
                playerY - weapon.getSprite().getHeight() / 2
            );
        }

        updateReloading(deltaTime);

        weapon.getSprite().draw(Main.getBatch());

        if (isReloading) {
            drawReloadBar();
        }

        updateBullets(deltaTime);
    }

    private void updateScreenCenter() {
        screenCenterX = Gdx.graphics.getWidth() / 2f;
        screenCenterY = Gdx.graphics.getHeight() / 2f;
    }

    private void updateReloading(float deltaTime) {
        if (isReloading) {
            reloadTimer += deltaTime;

            if (reloadTimer >= reloadDuration) {
                completeReload();
            }
        }
    }

    private void completeReload() {
        isReloading = false;
        reloadTimer = 0;

        if (weapon.getWeapon() != null) {
            weapon.setAmmo(weapon.getWeapon().getAmmoMax());
        } else {
            weapon.setAmmo(30);
        }
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

    public void handleWeaponRotation(int x, int y) {
        Sprite weaponSprite = weapon.getSprite();

        float playerX = 0;
        float playerY = 0;

        if (playerController != null) {
            playerX = playerController.getPlayer().getPosX();
            playerY = playerController.getPlayer().getPosY();
        }

        float angle = (float) Math.atan2(y - playerY, x - playerX);
        float degrees = (float) Math.toDegrees(angle);

        boolean shouldFlip = (degrees > 90 && degrees < 270) || (degrees < -90 && degrees > -270);

        if (weaponSprite.isFlipY() != shouldFlip) {
            weaponSprite.setFlip(false, shouldFlip);
        }

        weaponSprite.setRotation(degrees);
    }

    public void handleWeaponShoot(int x, int y) {
        if (isReloading || weapon.getAmmo() <= 0) {
            if (weapon.getAmmo() <= 0 && !isReloading) {
                startReload();
            }
            return;
        }

        GameAssetManager.getGameAssetManager().playShot();

        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        int projectileCount = 1;
        float bulletSpeed = 10.0f;
        int bulletDamage = 5;

        if (weapon.getWeapon() != null) {
            projectileCount = weapon.getWeapon().getProjectileCount();

            if (weapon.getWeapon() == Weapons.Shotgun) {
                bulletSpeed = 8.0f;
                bulletDamage = 10;
            } else if (weapon.getWeapon() == Weapons.Dual_Smg) {
                bulletSpeed = 15.0f;
                bulletDamage = 3;
            } else if (weapon.getWeapon() == Weapons.Revolver) {
                bulletSpeed = 12.0f;
                bulletDamage = 8;
            }
        }

        for (int i = 0; i < projectileCount; i++) {
            Bullet newBullet = new Bullet((int) playerX, (int) playerY);
            newBullet.setDamage(bulletDamage);

            Vector2 direction = new Vector2(x - playerX, y - playerY).nor();

            if (projectileCount > 1) {
                float spreadAngle = 15f;
                float angle = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));

                float bulletAngle = angle + (i - (projectileCount - 1) / 2f) * (spreadAngle / (projectileCount - 1));

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

    public void startReload() {
        if (!isReloading && weapon.getAmmo() < weapon.getWeapon().getAmmoMax()) {
            isReloading = true;
            reloadTimer = 0;

            GameAssetManager.getGameAssetManager().playReloadSound();
        }
    }

    public void cancelReload() {
        if (isReloading) {
            isReloading = false;
            reloadTimer = 0;
        }
    }

    public void updateBullets(float deltaTime) {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            // Skip or remove inactive bullets
            if (!bullet.isActive()) {
                iterator.remove();
                continue;
            }

            // Update bullet physics
            bullet.update(deltaTime);

            // Draw the bullet
            bullet.getSprite().draw(Main.getBatch());

            // Check if bullet is too far from player
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

        // Update reload duration
        if (weapon.getWeapon() != null) {
            this.reloadDuration = weapon.getWeapon().getReloadTime();
        }
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
        // Clear all bullets
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
    }
}
